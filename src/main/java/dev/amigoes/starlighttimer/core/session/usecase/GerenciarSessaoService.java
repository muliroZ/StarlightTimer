package dev.amigoes.starlighttimer.core.session.usecase;

import dev.amigoes.starlighttimer.core.identity.currentuser.UsuarioAtualPort;
import dev.amigoes.starlighttimer.core.identity.domain.ConfiguracoesUsuario;
import dev.amigoes.starlighttimer.core.identity.usecase.ConfiguracoesUsuarioService;
import dev.amigoes.starlighttimer.core.identity.usecase.PerfilService;
import dev.amigoes.starlighttimer.core.interfaces.GravadorEventos;
import dev.amigoes.starlighttimer.core.session.domain.ConclusaoAntecipadaException;
import dev.amigoes.starlighttimer.core.session.domain.Sessao;
import dev.amigoes.starlighttimer.core.session.domain.SessaoAtivaExistenteException;
import dev.amigoes.starlighttimer.core.session.domain.SessaoEvento;
import dev.amigoes.starlighttimer.core.session.persistence.SessaoRepository;
import dev.amigoes.starlighttimer.enums.session.AcaoSessao;
import dev.amigoes.starlighttimer.enums.session.MotivoAbandonoSessao;
import dev.amigoes.starlighttimer.enums.session.TipoSessao;
import dev.amigoes.starlighttimer.events.SessaoConcluida;
import dev.amigoes.starlighttimer.events.kafka.EventoEnvelope;
import dev.amigoes.starlighttimer.events.kafka.EventoMetadata;
import dev.amigoes.starlighttimer.events.kafka.TipoEvento;
import dev.amigoes.starlighttimer.events.kafka.payloads.SessaoAbandonadaPayloadV1;
import dev.amigoes.starlighttimer.events.kafka.payloads.SessaoConcluidaPayloadV1;
import dev.amigoes.starlighttimer.events.kafka.payloads.SessaoIniciadaPayloadV1;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.records.session.ResultadoTransicao;
import dev.amigoes.starlighttimer.records.session.SessaoIniciada;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.amigoes.starlighttimer.enums.session.TipoEvento.ABANDONADA;

@Service
public class GerenciarSessaoService {
    private final UsuarioAtualPort usuarioAtual;
    private final ConfiguracoesUsuarioService configuracoes;
    private final PerfilService perfil;
    private final SessaoRepository sessoes;
    private final TransicoesSessaoService transicoes;
    private final ApplicationEventPublisher eventos;
    private final GravadorEventos gravador;
    private final Clock clock;

    public GerenciarSessaoService(
            UsuarioAtualPort usuarioAtual,
            ConfiguracoesUsuarioService configuracoes,
            PerfilService perfil,
            SessaoRepository sessoes,
            TransicoesSessaoService transicoes,
            ApplicationEventPublisher eventos,
            GravadorEventos gravador, Clock clock
    ) {
        this.usuarioAtual = usuarioAtual;
        this.configuracoes = configuracoes;
        this.perfil = perfil;
        this.sessoes = sessoes;
        this.transicoes = transicoes;
        this.eventos = eventos;
        this.gravador = gravador;
        this.clock = clock;
    }

    @Transactional
    public Sessao iniciar(TipoSessao tipo, Integer duracaoPlanejada, OffsetDateTime reportadoEm) {
        UUID usuarioId = usuarioAtual.getUsuarioId();

        sessoes.buscarAtivaPorUsuario(usuarioId).ifPresent(ativa -> {
            throw new SessaoAtivaExistenteException(ativa.getId());
        });

        if (tipo == null || tipo == TipoSessao.LONG_BREAK) {
            throw new IllegalArgumentException("Tipo de sessao invalido para o MVP");
        }

        ConfiguracoesUsuario config = configuracoes.obterConfiguracoes();
        int duracao = duracaoPlanejada != null ? duracaoPlanejada
                : tipo == TipoSessao.FOCUS ? config.getFocoEmSegundos() : config.getPausaEmSegundos();

        if (tipo == TipoSessao.FOCUS && (duracao < 900 || duracao > 5400 || duracao % 300 != 0)
                || tipo == TipoSessao.SHORT_BREAK && (duracao < 180 || duracao > 900)) {
            throw new IllegalArgumentException("Duracao planejada invalida");
        }

        SessaoIniciada iniciada = transicoes.iniciar(usuarioId, tipo, duracao,
                ZoneId.of(perfil.obterPerfil().getFusoHorario()), reportadoEm);

        Sessao sessaoSalva = sessoes.salvarComEvento(iniciada.sessao(), iniciada.evento());
        EventoEnvelope<SessaoIniciadaPayloadV1> evento = new EventoEnvelope<>(
                EventoMetadata.gerar(TipoEvento.SESSAO_INICIADA, sessaoSalva.getId(), clock, 1),
                new SessaoIniciadaPayloadV1(usuarioId, tipo, sessaoSalva.getDuracaoPlanejada())
        );
        
        gravador.gravar(evento);
        return sessaoSalva;
    }

    @Transactional(noRollbackFor = ConclusaoAntecipadaException.class)
    public ResultadoTransicao transicionar(UUID id, AcaoSessao acao, OffsetDateTime reportadoEm) {
        Sessao sessao = sessoes.buscarPorIdEUsuarioParaAtualizar(id, usuarioAtual.getUsuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));

        SessaoEvento evento;
        try {
            evento = switch (acao) {
                case PAUSAR -> transicoes.pausar(sessao, reportadoEm);
                case RETOMAR -> transicoes.retomar(sessao, reportadoEm);
                case ABANDONAR -> transicoes.abandonar(sessao, "Abandonada pelo usuario", reportadoEm);
                case CONCLUIR -> transicoes.concluir(sessao, reportadoEm);
            };
        } catch (ConclusaoAntecipadaException ex) {
            sessoes.salvarComEvento(sessao, sessao.conclusaoRejeitada(
                    ex.getSegundosRestantes(), transicoes.horarioServidor(), reportadoEm));
            throw ex;
        }

        sessoes.salvarComEvento(sessao, evento);
        if (evento.tipoEvento() == ABANDONADA) {
            registrarAbandono(sessao, acao == AcaoSessao.ABANDONAR
                    ? MotivoAbandonoSessao.ABANDONO_USUARIO
                    : MotivoAbandonoSessao.TETO_PAUSA_EXCEDIDO);
        }
        if (acao != AcaoSessao.CONCLUIR) return new ResultadoTransicao(sessao, null);

        SessaoConcluida concluida = publicarConclusao(sessao);
        return new ResultadoTransicao(sessao, concluida.recompensa());
    }

    @Transactional(readOnly = true)
    public Optional<Sessao> buscarAtiva() {
        return sessoes.buscarAtivaPorUsuario(usuarioAtual.getUsuarioId());
    }

    @Transactional(readOnly = true)
    public List<Sessao> listarHistorico(LocalDate dataInicio, LocalDate dataFim, int limite, int deslocamento) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Intervalo de datas invalido");
        }
        return sessoes.listarHistorico(usuarioAtual.getUsuarioId(), dataInicio, dataFim, limite, deslocamento);
    }

    public SessaoConcluida publicarConclusao(Sessao sessao) {
        SessaoConcluida evento = new SessaoConcluida(sessao.getId(), sessao.getUsuarioId(),
                sessao.getTipo(), sessao.getDuracaoPlanejada(), sessao.getTerminadoEm().toInstant());

        eventos.publishEvent(evento);
        gravador.gravar(new EventoEnvelope<>(
                EventoMetadata.gerar(TipoEvento.SESSAO_CONCLUIDA, sessao.getId(), clock, 1),
                new SessaoConcluidaPayloadV1(sessao.getUsuarioId(), sessao.getTipo(), sessao.getTempoDeFoco())
        ));
        return evento;
    }

    void registrarAbandono(Sessao sessao, MotivoAbandonoSessao motivo) {
        gravador.gravar(new EventoEnvelope<>(
                EventoMetadata.gerar(TipoEvento.SESSAO_ABANDONADA, sessao.getId(), clock, 1),
                new SessaoAbandonadaPayloadV1(sessao.getUsuarioId(), sessao.getTipo(), motivo)
        ));
    }
}
