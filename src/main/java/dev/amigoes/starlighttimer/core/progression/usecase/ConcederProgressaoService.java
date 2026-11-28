package dev.amigoes.starlighttimer.core.progression.usecase;

import dev.amigoes.starlighttimer.core.interfaces.GravadorEventos;
import dev.amigoes.starlighttimer.core.progression.domain.CalculadoraXp;
import dev.amigoes.starlighttimer.core.progression.domain.CurvaDeNiveis;
import dev.amigoes.starlighttimer.core.progression.domain.ProgressoInsignia;
import dev.amigoes.starlighttimer.core.progression.persistence.CatalogoProgressaoRepository;
import dev.amigoes.starlighttimer.core.progression.persistence.ConstelacaoUsuarioRepository;
import dev.amigoes.starlighttimer.core.progression.persistence.GanhoXpRepository;
import dev.amigoes.starlighttimer.core.progression.persistence.InsigniaUsuarioRepository;
import dev.amigoes.starlighttimer.core.progression.persistence.ProgressaoRepository;
import dev.amigoes.starlighttimer.events.SessaoConcluida;
import dev.amigoes.starlighttimer.events.kafka.EventoEnvelope;
import dev.amigoes.starlighttimer.events.kafka.EventoMetadata;
import dev.amigoes.starlighttimer.events.kafka.TipoEvento;
import dev.amigoes.starlighttimer.events.kafka.payloads.ConstelacaoDesbloqueadaPayloadV1;
import dev.amigoes.starlighttimer.events.kafka.payloads.InsigniaDesbloqueadaPayloadV1;
import dev.amigoes.starlighttimer.records.progression.Constelacao;
import dev.amigoes.starlighttimer.records.progression.GanhoXp;
import dev.amigoes.starlighttimer.records.progression.Insignia;

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
    private final ProgressaoRepository repositorio;
    private final CatalogoProgressaoRepository catalogo;
    private final GanhoXpRepository ledger;
    private final InsigniaUsuarioRepository progressos;
    private final ConstelacaoUsuarioRepository constelacoes;
    private final GravadorEventos gravador;
    private final Clock relogio;
    private final CalculadoraXp calculadoraXp = new CalculadoraXp();
    private final CalculadoraMetricasInsignias metricas = new CalculadoraMetricasInsignias();

    public ConcederProgressaoService(ProgressaoRepository repositorio,
            CatalogoProgressaoRepository catalogo, GanhoXpRepository ledger,
            InsigniaUsuarioRepository progressos, ConstelacaoUsuarioRepository constelacoes,
            GravadorEventos gravador, Clock relogio) {
        this.repositorio = repositorio;
        this.catalogo = catalogo;
        this.ledger = ledger;
        this.progressos = progressos;
        this.constelacoes = constelacoes;
        this.gravador = gravador;
        this.relogio = relogio;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public SessaoConcluida.Recompensa conceder(SessaoConcluida evento) {
        int xpAnterior = repositorio.bloquearEObterTotal(evento.usuarioId());
        var dados = repositorio.carregarHistorico(evento.usuarioId());
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
            boolean novoDesbloqueio = desbloqueioAnterior == null && desbloqueadoEm != null;
            if (melhor > 0 || anterior.isPresent()) {
                progressos.registrarProgresso(new ProgressoInsignia(
                        evento.usuarioId(), insignia.id(), melhor, desbloqueadoEm));
                if (novoDesbloqueio) {
                    gravador.gravar(new EventoEnvelope<>(
                            EventoMetadata.gerar(TipoEvento.INSIGNIA_DESBLOQUEADA, evento.usuarioId(), relogio, 1),
                            new InsigniaDesbloqueadaPayloadV1(insignia.id())
                    ));
                }
            }
            if (desbloqueadoEm != null) {
                insigniasDesbloqueadas.add(insignia.id());
                if (novoDesbloqueio && ledger.registrarGanhoXp(ganho(
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
                gravador.gravar(new EventoEnvelope<>(
                        EventoMetadata.gerar(TipoEvento.CONSTELACAO_DESBLOQUEADA, evento.usuarioId(), relogio, 1),
                        new ConstelacaoDesbloqueadaPayloadV1(constelacao.id())
                ));
                if (ledger.registrarGanhoXp(ganho(evento.usuarioId(),
                        calculadoraXp.calcularRecompensaConstelacao(constelacao.xpRecompensa()),
                        null, constelacao.id(), null, agora))) {
                    xpGanho += constelacao.xpRecompensa();
                    novasConstelacoes.add(constelacao.codigo());
                }
            }
        }

        if (xpGanho != 0) repositorio.adicionar(evento.usuarioId(), xpGanho);
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
