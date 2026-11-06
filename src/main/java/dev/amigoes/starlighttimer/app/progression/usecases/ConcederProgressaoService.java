package dev.amigoes.starlighttimer.app.progression.usecases;

import dev.amigoes.starlighttimer.app.progression.ports.out.AtualizarXpPerfilPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerCatalogoProgressaoPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerHistoricoProgressaoPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.RegistrarConstelacaoUsuarioPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.RegistrarGanhoXpPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.RegistrarProgressoInsigniaPort;
import dev.amigoes.starlighttimer.domain.progression.entities.Constelacao;
import dev.amigoes.starlighttimer.domain.progression.entities.GanhoXp;
import dev.amigoes.starlighttimer.domain.progression.entities.Insignia;
import dev.amigoes.starlighttimer.domain.progression.entities.ProgressoInsignia;
import dev.amigoes.starlighttimer.domain.progression.services.CalculadoraXp;
import dev.amigoes.starlighttimer.domain.progression.services.CurvaDeNiveis;
import dev.amigoes.starlighttimer.domain.session.events.SessaoConcluida;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ConcederProgressaoService {
    private final AtualizarXpPerfilPort perfil;
    private final LerHistoricoProgressaoPort historico;
    private final LerCatalogoProgressaoPort catalogo;
    private final RegistrarGanhoXpPort ledger;
    private final RegistrarProgressoInsigniaPort progressos;
    private final RegistrarConstelacaoUsuarioPort constelacoes;
    private final Clock relogio;
    private final CalculadoraXp calculadoraXp = new CalculadoraXp();
    private final CalculadoraMetricasInsignias metricas = new CalculadoraMetricasInsignias();

    public ConcederProgressaoService(AtualizarXpPerfilPort perfil, LerHistoricoProgressaoPort historico,
            LerCatalogoProgressaoPort catalogo, RegistrarGanhoXpPort ledger,
            RegistrarProgressoInsigniaPort progressos, RegistrarConstelacaoUsuarioPort constelacoes,
            Clock relogio) {
        this.perfil = perfil;
        this.historico = historico;
        this.catalogo = catalogo;
        this.ledger = ledger;
        this.progressos = progressos;
        this.constelacoes = constelacoes;
        this.relogio = relogio;
    }

    /** Executado na transação que concluiu e persistiu a sessão. */
    @Transactional(propagation = Propagation.MANDATORY)
    public SessaoConcluida.Recompensa conceder(SessaoConcluida evento) {
        int xpAnterior = perfil.bloquearEObterTotal(evento.usuarioId());
        var dados = historico.carregar(evento.usuarioId());
        if (dados.ciclos().stream().noneMatch(c -> c.id().equals(evento.sessaoId()))) {
            throw new IllegalStateException("A sessao concluida precisa estar persistida antes do evento");
        }

        int xpGanho = 0;
        OffsetDateTime agora = OffsetDateTime.ofInstant(relogio.instant(), ZoneOffset.UTC);
        if (evento.ehFoco()) {
            int xpSessao = calculadoraXp.calcular(evento);
            if (!ledger.registrarGanhoXp(ganho(evento.usuarioId(), xpSessao,
                    evento.sessaoId(), null, null, agora))) {
                int nivel = curva().calcular(xpAnterior).nivel();
                return new SessaoConcluida.Recompensa(0, List.of(), List.of(), false, nivel);
            }
            xpGanho += xpSessao;
        }

        var valores = metricas.calcular(dados);
        var novasInsignias = new ArrayList<String>();
        Set<UUID> insigniasDesbloqueadas = new HashSet<>();
        for (Insignia insignia : catalogo.buscarInsignias()) {
            Integer valor = valores.get(insignia.codigo());
            if (valor == null) {
                throw new IllegalStateException("Avaliador ausente para " + insignia.codigo());
            }
            var anterior = progressos.buscarProgresso(evento.usuarioId(), insignia.id());
            int melhor = Math.max(valor, anterior.map(ProgressoInsignia::getContagemProgresso).orElse(0));
            var desbloqueioAnterior = anterior.map(ProgressoInsignia::getDesbloqueadoEm).orElse(null);
            boolean elegivel = melhor >= insignia.contagemAlvo();
            var desbloqueadoEm = desbloqueioAnterior != null ? desbloqueioAnterior
                    : elegivel ? relogio.instant() : null;
            if (melhor > 0 || anterior.isPresent()) {
                progressos.registrarProgresso(new ProgressoInsignia(
                        evento.usuarioId(), insignia.id(), melhor, desbloqueadoEm));
            }
            if (desbloqueadoEm != null) {
                insigniasDesbloqueadas.add(insignia.id());
                if (desbloqueioAnterior == null && ledger.registrarGanhoXp(ganho(
                        evento.usuarioId(), calculadoraXp.calcularRecompensaInsignia(insignia.xpRecompensa()),
                        null, null, insignia.id(), agora))) {
                    xpGanho += insignia.xpRecompensa();
                    novasInsignias.add(insignia.codigo());
                }
            }
        }

        int estrelas = (int) dados.ciclos().stream().filter(c -> c.foco()).count();
        var novasConstelacoes = new ArrayList<String>();
        for (Constelacao constelacao : catalogo.buscarConstelacoes()) {
            long qtdInsignias = constelacao.insignias().stream()
                    .filter(insigniasDesbloqueadas::contains).count();
            if (estrelas >= constelacao.qtdEstrelas()
                    && qtdInsignias >= constelacao.qtdInsigniasRequeridas()
                    && constelacoes.registrarDesbloqueio(constelacao.id(), evento.usuarioId(), relogio.instant())) {
                if (ledger.registrarGanhoXp(ganho(evento.usuarioId(),
                        calculadoraXp.calcularRecompensaConstelacao(constelacao.xpRecompensa()),
                        null, constelacao.id(), null, agora))) {
                    xpGanho += constelacao.xpRecompensa();
                    novasConstelacoes.add(constelacao.codigo());
                }
            }
        }

        if (xpGanho != 0) perfil.adicionar(evento.usuarioId(), xpGanho);
        var curva = curva();
        int nivelAnterior = curva.calcular(xpAnterior).nivel();
        int novoNivel = curva.calcular(Math.addExact(xpAnterior, xpGanho)).nivel();
        return new SessaoConcluida.Recompensa(xpGanho, novasInsignias, novasConstelacoes,
                novoNivel > nivelAnterior, novoNivel);
    }

    private CurvaDeNiveis curva() {
        return new CurvaDeNiveis(catalogo.buscarLimites(), catalogo.buscarPatentes());
    }

    private GanhoXp ganho(UUID usuarioId, int quantidade, UUID sessao,
                           UUID constelacao, UUID insignia, OffsetDateTime agora) {
        return new GanhoXp(UUID.randomUUID(), usuarioId, quantidade,
                sessao, constelacao, insignia, agora);
    }
}
