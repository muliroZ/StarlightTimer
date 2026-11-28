package dev.amigoes.starlighttimer;

import dev.amigoes.starlighttimer.core.identity.usecase.CadastrarUsuarioService;
import dev.amigoes.starlighttimer.records.identity.CadastroUsuarioComando;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class SegurancaJwtTest extends AbstractIntegrationTest {

    private static final String PERFIL = "/api/v1/me/perfil";
    private static final String ORIGEM_DEV = "http://localhost:5173";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtEncoder encoder;

    @Autowired
    private CadastrarUsuarioService cadastro;

    @Test
    void exigeTokenEmRotaProtegida() throws Exception {
        verificarFalhaDoBearer(mvc.perform(get(PERFIL)));
    }

    @Test
    void rejeitaTokenMalformado() throws Exception {
        verificarFalhaDoBearer(mvc.perform(get(PERFIL).header(HttpHeaders.AUTHORIZATION, "Bearer abc")));
    }

    @Test
    void rejeitaTokenExpiradoAlemDaTolerancia() throws Exception {
        var claims = JwtClaimsSet.builder()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Instant.now().minusSeconds(600))
                .expiresAt(Instant.now().minusSeconds(120))
                .build();
        verificarFalhaDoBearer(mvc.perform(get(PERFIL)
                .header(HttpHeaders.AUTHORIZATION, bearer(encoder, claims))));
    }

    @Test
    void rejeitaTokenSemExpiracao() throws Exception {
        var claims = JwtClaimsSet.builder().subject(UUID.randomUUID().toString()).build();
        verificarFalhaDoBearer(mvc.perform(get(PERFIL)
                .header(HttpHeaders.AUTHORIZATION, bearer(encoder, claims))));
    }

    @Test
    void rejeitaTokenComSujeitoInvalido() throws Exception {
        verificarFalhaDoBearer(mvc.perform(get(PERFIL)
                .header(HttpHeaders.AUTHORIZATION, bearer(encoder, claimsValidas("1-1-1-1-1")))));
    }

    @Test
    void rejeitaTokenAssinadoComOutraChave() throws Exception {
        var chave = new SecretKeySpec("outra-chave-ficticia-exclusiva-de-teste".getBytes(UTF_8), "HmacSHA256");
        var outroEncoder = NimbusJwtEncoder.withSecretKey(chave).algorithm(MacAlgorithm.HS256).build();
        verificarFalhaDoBearer(mvc.perform(get(PERFIL)
                .header(HttpHeaders.AUTHORIZATION, bearer(outroEncoder, claimsValidas(UUID.randomUUID().toString())))));
    }

    @Test
    void rejeitaTokenDeUsuarioInexistente() throws Exception {
        verificarProblema401(mvc.perform(get(PERFIL)
                .header(HttpHeaders.AUTHORIZATION, bearer(encoder, claimsValidas(UUID.randomUUID().toString())))));
    }

    @Test
    void usaIdentidadeDoTokenQuandoHeaderIndicaOutroUsuario() throws Exception {
        UUID usuarioA = cadastrarUsuario("Usuario A");
        UUID usuarioB = cadastrarUsuario("Usuario B");

        mvc.perform(get(PERFIL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(encoder, claimsValidas(usuarioA.toString())))
                        .header("X-Usuario-Id", usuarioB.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(usuarioA.toString()))
                .andExpect(jsonPath("$.nomeExibido").value("Usuario A"));
    }

    @Test
    void disponibilizaSwaggerEHealthSemToken() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
        mvc.perform(get("/actuator/health"))
                .andExpect(resultado -> assertThat(resultado.getResponse().getStatus()).isNotEqualTo(401));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/auth/cadastro", "/api/v1/auth/login", "/api/v1/auth/refresh"})
    void liberaApenasPostNosCaminhosReservadosParaAutenticacao(String caminho) throws Exception {
        mvc.perform(post(caminho)).andExpect(status().isNotFound());
        mvc.perform(get(caminho)).andExpect(status().isUnauthorized());
    }

    @Test
    void aceitaPreflightDaOrigemConfigurada() throws Exception {
        mvc.perform(options(PERFIL)
                        .header(HttpHeaders.ORIGIN, ORIGEM_DEV)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ORIGEM_DEV))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void recusaPreflightDeOrigemNaoConfigurada() throws Exception {
        mvc.perform(options(PERFIL)
                        .header(HttpHeaders.ORIGIN, "https://outra-origem.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    private UUID cadastrarUsuario(String nome) {
        return cadastro.cadastrar(new CadastroUsuarioComando(
                UUID.randomUUID() + "@example.test", nome, "America/Recife")).getId();
    }

    private static JwtClaimsSet claimsValidas(String sujeito) {
        Instant agora = Instant.now();
        return JwtClaimsSet.builder().subject(sujeito).issuedAt(agora).expiresAt(agora.plusSeconds(300)).build();
    }

    private static String bearer(JwtEncoder encoder, JwtClaimsSet claims) {
        var cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        return "Bearer " + encoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
    }

    private static void verificarProblema401(ResultActions resultado) throws Exception {
        resultado.andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.instance").value(PERFIL));
    }

    private static void verificarFalhaDoBearer(ResultActions resultado) throws Exception {
        verificarProblema401(resultado);
        resultado.andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                .andExpect(jsonPath("$.title").value("Nao autenticado"))
                .andExpect(jsonPath("$.detail").value("Token ausente, invalido ou expirado"));
    }
}
