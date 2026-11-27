package dev.amigoes.starlighttimer;

import dev.amigoes.starlighttimer.config.UsuarioLocalConfig;
import dev.amigoes.starlighttimer.core.session.domain.TransicaoInvalidaException;
import dev.amigoes.starlighttimer.core.session.usecase.EncerrarSessoesVencidasService;
import dev.amigoes.starlighttimer.core.session.usecase.GerenciarSessaoService;
import dev.amigoes.starlighttimer.enums.session.AcaoSessao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.NoOpTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
class ConclusaoConcorrenteTest extends AbstractIntegrationTest {
    @Autowired GerenciarSessaoService gerenciar;
    @Autowired EncerrarSessoesVencidasService encerrarVencidas;
    @Autowired JdbcTemplate jdbc;
    @Autowired TransactionTemplate transacao;

    @TestConfiguration(proxyBeanMethods = false)
    static class Configuracao {
        @Bean
        TaskScheduler taskScheduler() {
            return new NoOpTaskScheduler();
        }
    }

    @Test
    void conclusaoPeloAgendadorImpedeSegundaConclusaoPelaApi() throws Exception {
        UUID sessaoId = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO sessao (id, usuario_id, tipo, status, duracao_planejada, tempo_de_foco,
                                    tempo_de_pausa, dia_local, iniciado_em)
                VALUES (?, ?, 'FOCUS', 'RUNNING', 1500, 0, 0, CURRENT_DATE, now() - INTERVAL '26 minutes')
                """, sessaoId, UsuarioLocalConfig.USUARIO_ID);

        var conclusaoDoJobGravada = new CompletableFuture<Void>();
        var liberarCommitDoJob = new CountDownLatch(1);
        var pidDaApi = new CompletableFuture<Integer>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var jobEmExecucao = executor.submit(() -> transacao.executeWithoutResult(status -> {
                try {
                    encerrarVencidas.encerrar();
                    conclusaoDoJobGravada.complete(null);
                    if (!liberarCommitDoJob.await(15, TimeUnit.SECONDS)) {
                        throw new AssertionError("O teste não liberou o commit do agendador");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                } catch (RuntimeException | Error e) {
                    conclusaoDoJobGravada.completeExceptionally(e);
                    throw e;
                }
            }));

            try {
                conclusaoDoJobGravada.get(10, TimeUnit.SECONDS);
                var apiEmExecucao = executor.submit(() -> transacao.executeWithoutResult(status -> {
                    pidDaApi.complete(jdbc.queryForObject("SELECT pg_backend_pid()", Integer.class));
                    gerenciar.transicionar(sessaoId, AcaoSessao.CONCLUIR, null);
                }));
                int pid = pidDaApi.get(3, TimeUnit.SECONDS);
                await().atMost(Duration.ofSeconds(5)).until(() -> jdbc.queryForObject(
                        "SELECT cardinality(pg_blocking_pids(?)) > 0", Boolean.class, pid));

                liberarCommitDoJob.countDown();
                jobEmExecucao.get(5, TimeUnit.SECONDS);
                var falha = assertThrows(ExecutionException.class, () -> apiEmExecucao.get(5, TimeUnit.SECONDS));
                assertInstanceOf(TransicaoInvalidaException.class, falha.getCause());
            } finally {
                liberarCommitDoJob.countDown();
            }
        }

        assertEquals(1L, jdbc.queryForObject("""
                SELECT count(*) FROM outbox
                WHERE id_agregado = ? AND tipo_evento = 'SESSAO_CONCLUIDA'
                """, Long.class, sessaoId));
    }
}
