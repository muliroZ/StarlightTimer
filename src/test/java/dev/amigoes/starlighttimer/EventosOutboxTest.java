package dev.amigoes.starlighttimer;

import dev.amigoes.starlighttimer.config.UsuarioLocalConfig;
import dev.amigoes.starlighttimer.core.session.usecase.EncerrarSessoesVencidasService;
import dev.amigoes.starlighttimer.core.session.usecase.GerenciarSessaoService;
import dev.amigoes.starlighttimer.core.social.usecase.PedidoAmizadeService;
import dev.amigoes.starlighttimer.enums.session.AcaoSessao;
import dev.amigoes.starlighttimer.enums.session.TipoSessao;
import dev.amigoes.starlighttimer.enums.social.AcaoPedidoAmizade;
import dev.amigoes.starlighttimer.events.kafka.TipoEvento;
import dev.amigoes.starlighttimer.exceptions.ConflitoException;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.records.social.ResultadoPedidoAmizade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.NoOpTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("local")
class EventosOutboxTest extends AbstractIntegrationTest {
    @Autowired GerenciarSessaoService sessoes;
    @Autowired EncerrarSessoesVencidasService agendador;
    @Autowired PedidoAmizadeService pedidos;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper json;

    @TestConfiguration(proxyBeanMethods = false)
    static class Configuracao {
        @Bean
        TaskScheduler taskScheduler() {
            return new NoOpTaskScheduler();
        }
    }

    @Test
    void abandonoPeloUsuarioAposRetomadaNormal() {
        UUID sessaoId = sessoes.iniciar(TipoSessao.FOCUS, 1500, null).getId();
        sessoes.transicionar(sessaoId, AcaoSessao.PAUSAR, null);
        sessoes.transicionar(sessaoId, AcaoSessao.RETOMAR, null);
        assertTrue(buscarEventos(sessaoId, TipoEvento.SESSAO_ABANDONADA).isEmpty());

        sessoes.transicionar(sessaoId, AcaoSessao.ABANDONAR, null);
        verificarAbandono(sessaoId, "ABANDONO_USUARIO");
    }

    @Test
    void retomadaComPausaExpiradaRegistraAbandono() {
        UUID sessaoId = criarSessaoComPausaExpirada();

        sessoes.transicionar(sessaoId, AcaoSessao.RETOMAR, null);

        verificarAbandono(sessaoId, "TETO_PAUSA_EXCEDIDO");
    }

    @Test
    void agendadorRegistraAbandonoApenasNaPrimeiraExecucao() {
        UUID sessaoId = criarSessaoComPausaExpirada();

        agendador.encerrar();
        agendador.encerrar();

        verificarAbandono(sessaoId, "TETO_PAUSA_EXCEDIDO");
    }

    @Test
    void envioDePedidoGravaEventoApenasQuandoCriaPedidoPendente() {
        UUID destinatarioId = criarUsuario();

        UUID pedidoId = pedidos.enviar(destinatarioId).getId();
        assertThrows(ConflitoException.class, () -> pedidos.enviar(destinatarioId));

        assertEquals("PENDENTE", jdbc.queryForObject(
                "SELECT status FROM pedido_amizade WHERE id = ?", String.class, pedidoId));
        assertEquals(1L, jdbc.queryForObject("""
                SELECT count(*) FROM outbox
                WHERE tipo_evento = 'PEDIDO_AMIZADE_ENVIADO' AND payload->'payload'->>'destinatario_id' = ?
                """, Long.class, destinatarioId.toString()));
        verificarEvento(pedidoId, TipoEvento.PEDIDO_AMIZADE_ENVIADO, """
                {"remetente_id":"%s","destinatario_id":"%s"}
                """.formatted(UsuarioLocalConfig.USUARIO_ID, destinatarioId));
    }

    @Test
    void aceiteMantemIdDoPedidoEIncluiIdDaAmizadeCriadaPeloTrigger() {
        UUID destinatarioId = criarUsuario();
        UUID pedidoId = pedidos.enviar(destinatarioId).getId();

        ResultadoPedidoAmizade resultado = comoUsuario(destinatarioId,
                () -> pedidos.responder(pedidoId, AcaoPedidoAmizade.ACEITAR));
        UUID amizadeId = assertInstanceOf(ResultadoPedidoAmizade.Aceito.class, resultado).amizade().id();
        assertThrows(RecursoNaoEncontradoException.class, () -> comoUsuario(destinatarioId,
                () -> pedidos.responder(pedidoId, AcaoPedidoAmizade.ACEITAR)));

        assertEquals(0L, jdbc.queryForObject("SELECT count(*) FROM pedido_amizade WHERE id = ?", Long.class, pedidoId));
        verificarEvento(pedidoId, TipoEvento.PEDIDO_AMIZADE_ACEITO, """
                {"remetente_id":"%s","destinatario_id":"%s","amizade_id":"%s"}
                """.formatted(UsuarioLocalConfig.USUARIO_ID, destinatarioId, amizadeId));
    }

    @Test
    void recusaDePedidoNaoGravaEventoDeAceite() {
        UUID destinatarioId = criarUsuario();
        UUID pedidoId = pedidos.enviar(destinatarioId).getId();

        comoUsuario(destinatarioId, () -> pedidos.responder(pedidoId, AcaoPedidoAmizade.RECUSAR));

        assertTrue(buscarEventos(pedidoId, TipoEvento.PEDIDO_AMIZADE_ACEITO).isEmpty());
    }

    @Test
    void primeiroDesbloqueioDeInsigniaGravaEventoMesmoComXpJaRegistrado() {
        UUID usuarioId = criarUsuarioComPerfil();
        UUID insigniaId = jdbc.queryForObject(
                "SELECT id FROM insignia WHERE codigo = 'BADGE_FIRST_LIGHT'", UUID.class);
        jdbc.update("""
                INSERT INTO ganho_xp (id, usuario_id, quantidade, origem_insignia)
                VALUES (gen_random_uuid(), ?, 50, ?)
                """, usuarioId, insigniaId);
        jdbc.update("UPDATE perfil SET xp_total = 50 WHERE usuario_id = ?", usuarioId);

        concluirSessaoDeFoco(usuarioId);
        concluirSessaoDeFoco(usuarioId);

        assertEquals(1L, jdbc.queryForObject("""
                SELECT count(*) FROM insignia_usuario
                WHERE usuario_id = ? AND insignia_id = ? AND desbloqueado_em IS NOT NULL
                """, Long.class, usuarioId, insigniaId));
        verificarEvento(usuarioId, TipoEvento.INSIGNIA_DESBLOQUEADA, """
                {"insignia_id":"%s"}
                """.formatted(insigniaId));
    }

    @Test
    void constelacaoGravaEventoSomenteAoInserirPrimeiroDesbloqueio() {
        UUID usuarioId = criarUsuarioComPerfil();
        UUID constelacaoId = jdbc.queryForObject(
                "SELECT id FROM constelacao WHERE codigo = 'THE_FIRST_FIRMAMENT'", UUID.class);
        jdbc.update("""
                INSERT INTO sessao (id, usuario_id, tipo, status, duracao_planejada, tempo_de_foco,
                                    tempo_de_pausa, dia_local, iniciado_em, terminado_em)
                SELECT gen_random_uuid(), ?, 'FOCUS', 'COMPLETED', 1500, 1500, 0, CURRENT_DATE - dia,
                       now() - dia * INTERVAL '1 day' - INTERVAL '25 minutes',
                       now() - dia * INTERVAL '1 day'
                FROM generate_series(1, 8) AS historico(dia)
                """, usuarioId);

        concluirSessaoDeFoco(usuarioId);
        assertTrue(buscarEventos(usuarioId, TipoEvento.CONSTELACAO_DESBLOQUEADA).isEmpty());

        concluirSessaoDeFoco(usuarioId);
        concluirSessaoDeFoco(usuarioId);

        assertEquals(1L, jdbc.queryForObject("""
                SELECT count(*) FROM constelacao_usuario WHERE usuario_id = ? AND constelacao_id = ?
                """, Long.class, usuarioId, constelacaoId));
        verificarEvento(usuarioId, TipoEvento.CONSTELACAO_DESBLOQUEADA, """
                {"constelacao_id":"%s"}
                """.formatted(constelacaoId));
    }

    private UUID criarUsuarioComPerfil() {
        UUID usuarioId = criarUsuario();
        jdbc.update("INSERT INTO perfil (usuario_id, nome_exibido, fuso_horario) VALUES (?, 'Teste', 'UTC')",
                usuarioId);
        return usuarioId;
    }

    private void concluirSessaoDeFoco(UUID usuarioId) {
        UUID sessaoId = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO sessao (id, usuario_id, tipo, status, duracao_planejada, tempo_de_foco,
                                    tempo_de_pausa, dia_local, iniciado_em)
                VALUES (?, ?, 'FOCUS', 'RUNNING', 1500, 0, 0, CURRENT_DATE, now() - INTERVAL '26 minutes')
                """, sessaoId, usuarioId);
        comoUsuario(usuarioId, () -> sessoes.transicionar(sessaoId, AcaoSessao.CONCLUIR, null));
    }

    private UUID criarUsuario() {
        UUID usuarioId = UUID.randomUUID();
        jdbc.update("INSERT INTO usuario (id, email) VALUES (?, ?)", usuarioId, usuarioId + "@example.test");
        return usuarioId;
    }

    private <T> T comoUsuario(UUID usuarioId, Supplier<T> operacao) {
        var requisicao = new MockHttpServletRequest();
        requisicao.addHeader("X-Usuario-Id", usuarioId.toString());
        RequestAttributes anteriores = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requisicao));
        try {
            return operacao.get();
        } finally {
            RequestContextHolder.setRequestAttributes(anteriores);
        }
    }

    private UUID criarSessaoComPausaExpirada() {
        UUID sessaoId = sessoes.iniciar(TipoSessao.FOCUS, 1500, null).getId();
        sessoes.transicionar(sessaoId, AcaoSessao.PAUSAR, null);
        jdbc.update("UPDATE sessao SET iniciado_em = now() - INTERVAL '12 minutes' WHERE id = ?", sessaoId);
        jdbc.update("""
                UPDATE sessao_evento SET ocorrido_em = now() - INTERVAL '11 minutes'
                WHERE sessao_id = ? AND tipo_evento = 'PAUSADA'
                """, sessaoId);
        return sessaoId;
    }

    private void verificarAbandono(UUID sessaoId, String motivo) {
        assertEquals("ABANDONED", jdbc.queryForObject(
                "SELECT status FROM sessao WHERE id = ?", String.class, sessaoId));
        verificarEvento(sessaoId, TipoEvento.SESSAO_ABANDONADA, """
                {"usuario_id":"%s","tipo_sessao":"FOCUS","motivo":"%s"}
                """.formatted(UsuarioLocalConfig.USUARIO_ID, motivo));
    }

    private List<JsonNode> buscarEventos(UUID agregadoId, TipoEvento tipo) {
        return jdbc.queryForList("""
                SELECT payload::text FROM outbox WHERE id_agregado = ? AND tipo_evento = ?
                """, String.class, agregadoId, tipo.name()).stream().map(json::readTree).toList();
    }

    private void verificarEvento(UUID agregadoId, TipoEvento tipo, String payloadEsperado) {
        List<JsonNode> envelopes = buscarEventos(agregadoId, tipo);
        assertEquals(1, envelopes.size());
        JsonNode envelope = envelopes.getFirst();
        assertEquals(tipo.valor(), envelope.at("/metadata/tipo_evento").stringValue());
        assertEquals(tipo.derivarAgregado().valor(), envelope.at("/metadata/tipo_agregado").stringValue());
        assertEquals(agregadoId.toString(), envelope.at("/metadata/agregado_id").stringValue());
        assertEquals(1, envelope.at("/metadata/versao_payload").intValue());
        assertEquals(json.readTree(payloadEsperado), envelope.get("payload"));
    }
}
