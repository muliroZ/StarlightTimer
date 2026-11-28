package dev.amigoes.starlighttimer;

import dev.amigoes.starlighttimer.config.RelogioAjustavel;
import dev.amigoes.starlighttimer.core.session.usecase.EncerrarSessoesVencidasService;
import dev.amigoes.starlighttimer.enums.session.StatusSessao;
import dev.amigoes.starlighttimer.enums.session.TipoSessao;
import dev.amigoes.starlighttimer.enums.social.AcaoPedidoAmizade;
import dev.amigoes.starlighttimer.records.identity.CadastroContaRequest;
import dev.amigoes.starlighttimer.records.identity.ContaCriadaResponse;
import dev.amigoes.starlighttimer.records.identity.LoginRequest;
import dev.amigoes.starlighttimer.records.identity.TokensResponse;
import dev.amigoes.starlighttimer.records.session.IniciarSessaoRequest;
import dev.amigoes.starlighttimer.records.session.SessaoResponse;
import dev.amigoes.starlighttimer.records.session.TransicionarSessaoRequest;
import dev.amigoes.starlighttimer.records.social.AmizadeResponse;
import dev.amigoes.starlighttimer.records.social.EnviarPedidoAmizadeRequest;
import dev.amigoes.starlighttimer.records.social.PedidoAmizadeResponse;
import dev.amigoes.starlighttimer.records.social.ResponderPedidoAmizadeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.NoOpTaskScheduler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Execution(ExecutionMode.SAME_THREAD)
class FluxoApiTest extends AbstractIntegrationTest {
    private static final String AUTH = "/api/v1/auth";
    private static final String SESSOES = "/api/v1/sessoes";
    private static final String PROGRESSAO = "/api/v1/me/progressao";
    private static final String PEDIDOS = "/api/v1/amizades/pedidos";
    private static final String SENHA = "senha-exclusiva-do-teste";
    private static final int DURACAO_FOCO = 1500;

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired RelogioAjustavel relogio;
    @Autowired EncerrarSessoesVencidasService encerrarSessoes;

    @TestConfiguration(proxyBeanMethods = false)
    static class Configuracao {
        @Bean
        @Primary
        RelogioAjustavel relogioAjustavel() {
            return new RelogioAjustavel(Clock.systemUTC().instant().truncatedTo(ChronoUnit.SECONDS));
        }

        @Bean
        TaskScheduler taskScheduler() {
            return new NoOpTaskScheduler();
        }
    }

    @Test
    void fluxoCompletoDoCadastroAteProgressao() throws Exception {
        Conta conta = cadastrarELogar();
        UUID sessao = iniciarFoco(conta.token(), DURACAO_FOCO);

        relogio.avancar(Duration.ofSeconds(DURACAO_FOCO));
        transicionar(conta.token(), sessao, StatusSessao.COMPLETED)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.recompensa").exists());

        mvc.perform(autorizar(get(PROGRESSAO), conta.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.xpTotal").value(75))
                .andExpect(jsonPath("$.nivel").value(2))
                .andExpect(jsonPath("$.xpAtual").value(25))
                .andExpect(jsonPath("$.xpDoProximoNivel").value(125))
                .andExpect(jsonPath("$.xpParaProximoNivel").value(50))
                .andExpect(jsonPath("$.estrelasForjadas").value(1));

        verificarXpTotal(conta.id(), 75);
        verificarQuantidadeDeGanhosXp(conta.id(), 2);
        assertThat(jdbc.queryForObject("""
                SELECT quantidade FROM ganho_xp WHERE usuario_id = ? AND origem_sessao = ?
                """, Integer.class, conta.id(), sessao)).isEqualTo(25);
        assertThat(jdbc.queryForObject("""
                SELECT quantidade FROM ganho_xp WHERE usuario_id = ? AND origem_insignia IS NOT NULL
                """, Integer.class, conta.id())).isEqualTo(50);
    }

    @Test
    void conclusaoCincoSegundosAntesDoFimEhAceita() throws Exception {
        Conta conta = cadastrarELogar();
        UUID sessao = iniciarFoco(conta.token(), DURACAO_FOCO);

        relogio.avancar(Duration.ofSeconds(1495));
        var horarioDoPedido = relogio.instant();
        var resposta = transicionar(conta.token(), sessao, StatusSessao.COMPLETED)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        assertThat(lerResposta(resposta, SessaoResponse.class).terminadoEm().toInstant())
                .isEqualTo(horarioDoPedido);
    }

    @Test
    void conclusaoSeisSegundosAntesDoFimRetorna409() throws Exception {
        Conta conta = cadastrarELogar();
        UUID sessao = iniciarFoco(conta.token(), DURACAO_FOCO);

        relogio.avancar(Duration.ofSeconds(1494));
        transicionar(conta.token(), sessao, StatusSessao.COMPLETED)
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.segundosRestantes").value(6));

        assertThat(jdbc.queryForObject("SELECT status FROM sessao WHERE id = ?", String.class, sessao))
                .isEqualTo("RUNNING");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM sessao_evento WHERE sessao_id = ? AND tipo_evento = 'CONCLUSAO_REJEITADA'
                """, Long.class, sessao)).isEqualTo(1L);
        verificarQuantidadeDeGanhosXp(conta.id(), 0);
    }

    @Test
    void pausaDe600SegundosAindaPermiteRetomar() throws Exception {
        Conta conta = cadastrarELogar();
        UUID sessao = iniciarFoco(conta.token(), DURACAO_FOCO);

        relogio.avancar(Duration.ofSeconds(60));
        pausar(conta.token(), sessao);
        relogio.avancar(Duration.ofSeconds(600));

        transicionar(conta.token(), sessao, StatusSessao.RUNNING)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.tempoDePausa").value(600));
    }

    @Test
    void pausaDe601SegundosAbandonaSemXp() throws Exception {
        Conta conta = cadastrarELogar();
        UUID sessao = iniciarFoco(conta.token(), DURACAO_FOCO);

        relogio.avancar(Duration.ofSeconds(60));
        pausar(conta.token(), sessao);
        relogio.avancar(Duration.ofSeconds(601));

        transicionar(conta.token(), sessao, StatusSessao.RUNNING)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABANDONED"));

        verificarXpTotal(conta.id(), 0);
        verificarQuantidadeDeGanhosXp(conta.id(), 0);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM outbox WHERE id_agregado = ? AND tipo_evento = 'SESSAO_ABANDONADA'
                """, Long.class, sessao)).isEqualTo(1L);
    }

    @Test
    void jobConcluiSessaoVencidaComTerminoCalculado() throws Exception {
        Conta conta = cadastrarELogar();
        UUID sessao = iniciarFoco(conta.token(), DURACAO_FOCO);

        relogio.avancar(Duration.ofSeconds(30));
        pausar(conta.token(), sessao);
        relogio.avancar(Duration.ofSeconds(120));
        transicionar(conta.token(), sessao, StatusSessao.RUNNING)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.tempoDePausa").value(120));

        relogio.avancar(Duration.ofHours(1));
        encerrarSessoes.encerrar();

        var persistida = jdbc.queryForObject("""
                SELECT status, iniciado_em, terminado_em, tempo_de_foco FROM sessao WHERE id = ?
                """, (rs, numeroDaLinha) -> new SessaoPersistida(
                rs.getString("status"), rs.getObject("iniciado_em", OffsetDateTime.class),
                rs.getObject("terminado_em", OffsetDateTime.class), rs.getInt("tempo_de_foco")), sessao);

        assertThat(persistida).isNotNull();
        assertThat(persistida.status()).isEqualTo("COMPLETED");
        assertThat(persistida.terminadoEm().toInstant())
                .isEqualTo(persistida.iniciadoEm().toInstant().plusSeconds(1620));
        assertThat(persistida.tempoDeFoco()).isEqualTo(DURACAO_FOCO);
        assertThat(jdbc.queryForObject("""
                SELECT origem FROM sessao_evento WHERE sessao_id = ? AND tipo_evento = 'CONCLUIDA'
                """, String.class, sessao)).isEqualTo("AGENDADOR");
        assertThat(jdbc.queryForObject("""
                SELECT quantidade FROM ganho_xp WHERE usuario_id = ? AND origem_sessao = ?
                """, Integer.class, conta.id(), sessao)).isEqualTo(25);
    }

    @Test
    void aceiteDePedidoCriaAmizadePeloTriggerERemoveOPedido() throws Exception {
        Conta remetente = cadastrarELogar();
        Conta destinatario = cadastrarELogar();
        var envio = mvc.perform(autorizar(post(PEDIDOS), remetente.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new EnviarPedidoAmizadeRequest(destinatario.id()))))
                .andExpect(status().isCreated());
        UUID pedido = lerResposta(envio, PedidoAmizadeResponse.class).id();

        var aceite = mvc.perform(autorizar(patch(PEDIDOS + "/{id}", pedido), destinatario.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new ResponderPedidoAmizadeRequest(AcaoPedidoAmizade.ACEITAR))))
                .andExpect(status().isOk());
        var amizade = lerResposta(aceite, AmizadeResponse.class);

        var parEsperado = jdbc.queryForObject("""
                SELECT LEAST(?::uuid, ?::uuid) AS primeiro, GREATEST(?::uuid, ?::uuid) AS segundo
                """, (rs, numeroDaLinha) -> List.of(rs.getObject("primeiro", UUID.class),
                rs.getObject("segundo", UUID.class)), remetente.id(), destinatario.id(),
                remetente.id(), destinatario.id());
        assertThat(List.of(amizade.usuario1Id(), amizade.usuario2Id())).isEqualTo(parEsperado);

        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM amizade WHERE usuario1_id = ? AND usuario2_id = ?
                """, Long.class, amizade.usuario1Id(), amizade.usuario2Id())).isEqualTo(1L);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM pedido_amizade WHERE id = ?", Long.class, pedido))
                .isZero();
    }

    @Test
    void rotaProtegidaSemTokenRetorna401() throws Exception {
        mvc.perform(get(PROGRESSAO))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

        mvc.perform(post(SESSOES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new IniciarSessaoRequest(TipoSessao.FOCUS, DURACAO_FOCO, null))))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    private Conta cadastrarELogar() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        var cadastro = new CadastroContaRequest(email, SENHA, "Pessoa de teste", "America/Recife");
        var cadastroFeito = mvc.perform(post(AUTH + "/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(cadastro)))
                .andExpect(status().isCreated());
        UUID usuarioId = lerResposta(cadastroFeito, ContaCriadaResponse.class).id();

        var login = mvc.perform(post(AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new LoginRequest(email, SENHA))))
                .andExpect(status().isOk());
        String token = lerResposta(login, TokensResponse.class).accessToken();
        return new Conta(usuarioId, token);
    }

    private UUID iniciarFoco(String token, int duracao) throws Exception {
        var inicio = mvc.perform(autorizar(post(SESSOES), token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new IniciarSessaoRequest(TipoSessao.FOCUS, duracao, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.horarioServidor").exists());
        return lerResposta(inicio, SessaoResponse.class).id();
    }

    private void pausar(String token, UUID id) throws Exception {
        transicionar(token, id, StatusSessao.PAUSED)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));
    }

    private ResultActions transicionar(String token, UUID id, StatusSessao destino) throws Exception {
        return mvc.perform(autorizar(patch(SESSOES + "/{id}", id), token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new TransicionarSessaoRequest(destino, null))));
    }

    private MockHttpServletRequestBuilder autorizar(MockHttpServletRequestBuilder requisicao, String token) {
        return requisicao.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    private <T> T lerResposta(ResultActions resposta, Class<T> tipo) throws Exception {
        return json.readValue(resposta.andReturn().getResponse().getContentAsString(UTF_8), tipo);
    }

    private void verificarXpTotal(UUID usuario, int esperado) {
        assertThat(jdbc.queryForObject("SELECT xp_total FROM perfil WHERE usuario_id = ?", Integer.class, usuario))
                .isEqualTo(esperado);
    }

    private void verificarQuantidadeDeGanhosXp(UUID usuario, long esperada) {
        assertThat(jdbc.queryForObject("SELECT count(*) FROM ganho_xp WHERE usuario_id = ?", Long.class, usuario))
                .isEqualTo(esperada);
    }

    private record Conta(UUID id, String token) {}

    private record SessaoPersistida(String status, OffsetDateTime iniciadoEm, OffsetDateTime terminadoEm,
                                   int tempoDeFoco) {}
}
