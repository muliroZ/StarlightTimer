package dev.amigoes.starlighttimer;

import dev.amigoes.starlighttimer.core.identity.usecase.CadastrarUsuarioService;
import dev.amigoes.starlighttimer.records.identity.CadastroContaRequest;
import dev.amigoes.starlighttimer.records.identity.CadastroUsuarioComando;
import dev.amigoes.starlighttimer.records.identity.ContaCriadaResponse;
import dev.amigoes.starlighttimer.records.identity.LoginRequest;
import dev.amigoes.starlighttimer.records.identity.RefreshTokenRequest;
import dev.amigoes.starlighttimer.records.identity.TokensEmitidos;
import dev.amigoes.starlighttimer.records.identity.TokensResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class AutenticacaoTest extends AbstractIntegrationTest {

    private static final String AUTH = "/api/v1/auth";
    private static final String PERFIL = "/api/v1/me/perfil";
    private static final String SENHA = "senha-exclusiva-do-teste";
    private static final String NOME = "Pessoa de teste";
    private static final String FUSO = "America/Recife";
    private static final Instant AGORA = Instant.parse("2026-10-01T12:00:00Z");

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired JwtEncoder jwtEncoder;
    @Autowired JwtDecoder jwtDecoder;
    @Autowired CadastrarUsuarioService cadastroSemIdentidade;
    @Autowired TransactionTemplate transacao;

    @TestConfiguration(proxyBeanMethods = false)
    static class Configuracao {
        @Bean
        @Primary
        Clock relogioFixo() {
            return Clock.fixed(AGORA, ZoneOffset.UTC);
        }
    }

    @Test
    void cadastraAgregadoComHashSemExporSenha(CapturedOutput saida) throws Exception {
        String email = emailUnico();
        String corpo = mvc.perform(requisicao("/cadastro", cadastro(email, SENHA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.nomeExibido").value(NOME))
                .andExpect(jsonPath("$.fusoHorario").value(FUSO))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andReturn().getResponse().getContentAsString(UTF_8);
        UUID id = json.readValue(corpo, ContaCriadaResponse.class).id();

        String hash = jdbc.queryForObject(
                "SELECT senha_hash FROM identidade WHERE usuario_id = ? AND provedor = 'PASSWORD'",
                String.class, id);
        assertThat(hash).startsWith("$2").isNotEqualTo(SENHA);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM usuario u
                JOIN perfil p ON p.usuario_id = u.id
                JOIN configuracoes_usuario c ON c.usuario_id = u.id
                WHERE u.id = ?
                """, Long.class, id)).isEqualTo(1L);
        assertThat(corpo).doesNotContain(SENHA);
        assertThat(saida.getAll()).doesNotContain(SENHA);
    }

    @Test
    void cadastroDuplicadoRetornaProblema409() throws Exception {
        var conta = cadastrar(SENHA);

        mvc.perform(requisicao("/cadastro", cadastro(conta.email(), SENHA)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://starlighttimer.dev/errors/email-ja-cadastrado"));
    }

    @Test
    void loginEmiteTokensEAutorizaPerfilSemExporSenha(CapturedOutput saida) throws Exception {
        var conta = cadastrar(SENHA);
        String userAgent = "StarlightTimer/LoginTest";
        String corpo = mvc.perform(requisicao("/login", new LoginRequest(conta.email(), SENHA))
                        .header(HttpHeaders.USER_AGENT, userAgent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.tipoToken").value("Bearer"))
                .andExpect(jsonPath("$.expiraEmSegundos").value(3600))
                .andReturn().getResponse().getContentAsString(UTF_8);
        var tokens = json.readValue(corpo, TokensResponse.class);

        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).matches("[A-Za-z0-9_-]{43}");
        assertThat(Base64.getUrlDecoder().decode(tokens.refreshToken())).hasSize(32);
        var jwt = jwtDecoder.decode(tokens.accessToken());
        assertThat(jwt.getSubject()).isEqualTo(conta.id().toString());
        assertThat(jwt.getIssuedAt()).isEqualTo(AGORA);
        assertThat(jwt.getExpiresAt()).isEqualTo(AGORA.plus(Duration.ofHours(1)));
        assertThat(jwt.getId()).isNotBlank();
        mvc.perform(get(PERFIL).header(HttpHeaders.AUTHORIZATION, bearer(tokens.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(conta.id().toString()));

        String hashGravado = jdbc.queryForObject(
                "SELECT token_hash FROM refresh_token WHERE usuario_id = ?", String.class, conta.id());
        assertThat(hashGravado).isNotEqualTo(tokens.refreshToken()).isEqualTo(sha256(tokens.refreshToken()));
        assertThat(jdbc.queryForObject(
                "SELECT user_agent FROM refresh_token WHERE usuario_id = ?", String.class, conta.id()))
                .isEqualTo(userAgent);
        assertThat(jdbc.queryForObject("""
                SELECT criado_em = ? AND expira_em = ? FROM refresh_token WHERE usuario_id = ?
                """, Boolean.class, Timestamp.from(AGORA), Timestamp.from(AGORA.plus(Duration.ofDays(30))), conta.id()))
                .isTrue();
        assertThat(corpo).doesNotContain(SENHA);
        assertThat(saida.getAll()).doesNotContain(SENHA, tokens.accessToken(), tokens.refreshToken());
    }

    @Test
    void loginUsaMesmo401ParaCredencialErradaOuUsuarioInelegivel() throws Exception {
        var conta = cadastrar(SENHA);
        verificarLoginInvalido(conta.email(), "senha-errada");
        verificarLoginInvalido(emailUnico(), SENHA);

        String emailSemIdentidade = emailUnico();
        cadastroSemIdentidade.cadastrar(new CadastroUsuarioComando(emailSemIdentidade, NOME, FUSO));
        verificarLoginInvalido(emailSemIdentidade, SENHA);

        jdbc.update("UPDATE usuario SET status = 'DISABLED' WHERE id = ?", conta.id());
        verificarLoginInvalido(conta.email(), SENHA);
        jdbc.update("UPDATE usuario SET status = 'ACTIVE', excluido_em = ? WHERE id = ?",
                Timestamp.from(AGORA), conta.id());
        verificarLoginInvalido(conta.email(), SENHA);
    }

    @Test
    void aplicaTetoEmBytesUtf8NoCadastroENoLogin() throws Exception {
        String senhaNoLimite = "é".repeat(36);
        String acimaDoLimite = senhaNoLimite + "a";
        var conta = cadastrar(senhaNoLimite);
        login(conta.email(), senhaNoLimite);

        mvc.perform(requisicao("/cadastro", cadastro(emailUnico(), acimaDoLimite)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Dados de cadastro invalidos"))
                .andExpect(content().string(not(containsString(acimaDoLimite))));
        verificarLoginInvalido(conta.email(), acimaDoLimite);
    }

    @Test
    void rotacionaParNoMesmoInstanteMesmoComBearerExpirado() throws Exception {
        var conta = cadastrar(SENHA);
        var original = login(conta.email(), SENHA);
        String bearerExpirado = bearer(accessExpirado(conta.id()));
        String userAgent = "StarlightTimer/RefreshTest";

        var novo = tokens(mvc.perform(requisicao("/refresh", new RefreshTokenRequest(original.refreshToken()))
                        .header(HttpHeaders.AUTHORIZATION, bearerExpirado)
                        .header(HttpHeaders.USER_AGENT, userAgent)));
        assertThat(novo.accessToken()).isNotEqualTo(original.accessToken());
        assertThat(novo.refreshToken()).isNotEqualTo(original.refreshToken());
        assertThat(jwtDecoder.decode(novo.accessToken()).getIssuedAt()).isEqualTo(AGORA);
        assertThat(revogado(original.refreshToken())).isTrue();
        assertThat(jdbc.queryForObject(
                "SELECT user_agent FROM refresh_token WHERE token_hash = ?", String.class, sha256(novo.refreshToken())))
                .isEqualTo(userAgent);

        verificarRefreshInvalido(original.refreshToken());
        mvc.perform(get(PERFIL).header(HttpHeaders.AUTHORIZATION, bearerExpirado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejeitaRefreshInexistenteExpiradoOuDeUsuarioInativo() throws Exception {
        var conta = cadastrar(SENHA);
        var tokens = login(conta.email(), SENHA);

        verificarRefreshInvalido(UUID.randomUUID().toString());
        jdbc.update("UPDATE refresh_token SET expira_em = ? WHERE usuario_id = ?",
                Timestamp.from(AGORA.minusSeconds(1)), conta.id());
        verificarRefreshInvalido(tokens.refreshToken());
        jdbc.update("UPDATE refresh_token SET expira_em = ? WHERE usuario_id = ?",
                Timestamp.from(AGORA), conta.id());
        verificarRefreshInvalido(tokens.refreshToken());

        jdbc.update("UPDATE refresh_token SET expira_em = ? WHERE usuario_id = ?",
                Timestamp.from(AGORA.plusSeconds(60)), conta.id());
        jdbc.update("UPDATE usuario SET status = 'DISABLED' WHERE id = ?", conta.id());
        verificarRefreshInvalido(tokens.refreshToken());
        assertThat(revogado(tokens.refreshToken())).isFalse();
    }

    @Test
    void logoutRevogaApenasORefreshEPermaneceIdempotente() throws Exception {
        var conta = cadastrar(SENHA);
        var tokens = login(conta.email(), SENHA);
        var request = new RefreshTokenRequest(tokens.refreshToken());

        mvc.perform(requisicao("/logout", request)).andExpect(status().isNoContent());
        assertThat(revogado(tokens.refreshToken())).isTrue();
        verificarRefreshInvalido(tokens.refreshToken());
        mvc.perform(requisicao("/logout", request)).andExpect(status().isNoContent());
        mvc.perform(requisicao("/logout", new RefreshTokenRequest(UUID.randomUUID().toString())))
                .andExpect(status().isNoContent());
        mvc.perform(get(PERFIL).header(HttpHeaders.AUTHORIZATION, bearer(tokens.accessToken())))
                .andExpect(status().isOk());
    }

    @Test
    void refreshConcorrenteCriaUmUnicoSucessorAtivo() throws Exception {
        var conta = cadastrar(SENHA);
        String refreshToken = login(conta.email(), SENHA).refreshToken();
        var primeiroStatus = new CompletableFuture<Integer>();
        var liberarCommit = new CountDownLatch(1);
        var pidSegundoRefresh = new CompletableFuture<Integer>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var primeiroRefresh = executor.submit(() -> transacao.executeWithoutResult(status -> {
                primeiroStatus.complete(statusDoRefresh(refreshToken));
                aguardarCommit(liberarCommit);
            }));

            try {
                assertThat(primeiroStatus.get(10, TimeUnit.SECONDS)).isEqualTo(200);
                var segundoRefresh = executor.submit(() -> transacao.execute(status -> {
                    pidSegundoRefresh.complete(jdbc.queryForObject("SELECT pg_backend_pid()", Integer.class));
                    int resposta = statusDoRefresh(refreshToken);
                    status.setRollbackOnly();
                    return resposta;
                }));

                int pid = pidSegundoRefresh.get(5, TimeUnit.SECONDS);
                await().atMost(Duration.ofSeconds(5)).until(() -> jdbc.queryForObject(
                        "SELECT cardinality(pg_blocking_pids(?)) > 0", Boolean.class, pid));

                liberarCommit.countDown();
                primeiroRefresh.get(10, TimeUnit.SECONDS);
                assertThat(segundoRefresh.get(10, TimeUnit.SECONDS)).isEqualTo(401);
            } finally {
                liberarCommit.countDown();
            }
        }

        assertThat(revogado(refreshToken)).isTrue();
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM refresh_token WHERE usuario_id = ? AND revogado_em IS NULL
                """, Long.class, conta.id())).isEqualTo(1L);
    }

    @Test
    void migrationRejeitaIdentidadesInvalidasEDuplicadas() throws Exception {
        UUID id = cadastrar(SENHA).id();

        verificarIdentidadeInvalida(id, null, null, "hash", "provedor");
        verificarIdentidadeInvalida(id, "GOOGLE", "externo", null, "provedor_check");
        verificarIdentidadeInvalida(id, "PASSWORD", null, null, "identidade_password_check");
        verificarIdentidadeInvalida(id, "PASSWORD", "externo", "hash", "identidade_password_check");
        verificarIdentidadeInvalida(id, "PASSWORD", null, "outro-hash", "unique_usuario_provedor");
    }

    @Test
    void representacoesTextuaisOmitemSegredos() {
        String access = "access-secreto";
        String refresh = "refresh-secreto";

        assertThat(cadastro(emailUnico(), SENHA).toString()).doesNotContain(SENHA);
        assertThat(new LoginRequest(emailUnico(), SENHA).toString()).doesNotContain(SENHA);
        assertThat(new RefreshTokenRequest(refresh).toString()).doesNotContain(refresh);
        assertThat(new TokensEmitidos(access, refresh, 3600).toString()).doesNotContain(access, refresh);
        assertThat(new TokensResponse(access, refresh, "Bearer", 3600).toString()).doesNotContain(access, refresh);
    }

    private ContaCriadaResponse cadastrar(String senha) throws Exception {
        String corpo = mvc.perform(requisicao("/cadastro", cadastro(emailUnico(), senha)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(UTF_8);
        return json.readValue(corpo, ContaCriadaResponse.class);
    }

    private TokensResponse login(String email, String senha) throws Exception {
        return tokens(mvc.perform(requisicao("/login", new LoginRequest(email, senha))));
    }

    private TokensResponse tokens(ResultActions resultado) throws Exception {
        String corpo = resultado.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(UTF_8);
        return json.readValue(corpo, TokensResponse.class);
    }

    private void verificarLoginInvalido(String email, String senha) throws Exception {
        mvc.perform(requisicao("/login", new LoginRequest(email, senha)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Credenciais invalidas"));
    }

    private void verificarRefreshInvalido(String refreshToken) throws Exception {
        mvc.perform(requisicao("/refresh", new RefreshTokenRequest(refreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Refresh token invalido ou expirado"));
    }

    private void verificarIdentidadeInvalida(UUID usuarioId, String provedor, String idExterno,
                                             String senhaHash, String restricao) {
        var falha = assertThrows(DataIntegrityViolationException.class, () -> jdbc.update("""
                INSERT INTO identidade (id, usuario_id, provedor, id_externo, senha_hash)
                VALUES (?, ?, ?, ?, ?)
                """, UUID.randomUUID(), usuarioId, provedor, idExterno, senhaHash));
        assertThat(falha.getMostSpecificCause()).isInstanceOfSatisfying(PSQLException.class, causa -> {
            var erro = causa.getServerErrorMessage();
            assertThat(erro.getConstraint() == null ? erro.getColumn() : erro.getConstraint()).isEqualTo(restricao);
        });
    }

    private int statusDoRefresh(String refreshToken) {
        try {
            return mvc.perform(requisicao("/refresh", new RefreshTokenRequest(refreshToken)))
                    .andReturn().getResponse().getStatus();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao executar refresh", e);
        }
    }

    private static void aguardarCommit(CountDownLatch liberarCommit) {
        try {
            if (!liberarCommit.await(20, TimeUnit.SECONDS)) {
                throw new AssertionError("O teste não liberou o commit do primeiro refresh");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private boolean revogado(String refreshToken) throws Exception {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT revogado_em IS NOT NULL FROM refresh_token WHERE token_hash = ?",
                Boolean.class, sha256(refreshToken)));
    }

    private String accessExpirado(UUID usuarioId) {
        var claims = JwtClaimsSet.builder()
                .subject(usuarioId.toString())
                .issuedAt(AGORA.minusSeconds(600))
                .expiresAt(AGORA.minusSeconds(120))
                .build();
        var cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
    }

    private MockHttpServletRequestBuilder requisicao(String rota, Object corpo) {
        return post(AUTH + rota).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(corpo));
    }

    private static CadastroContaRequest cadastro(String email, String senha) {
        return new CadastroContaRequest(email, senha, NOME, FUSO);
    }

    private static String emailUnico() {
        return UUID.randomUUID() + "@example.test";
    }

    private static String bearer(String accessToken) {
        return "Bearer " + accessToken;
    }

    private static String sha256(String token) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(UTF_8)));
    }
}
