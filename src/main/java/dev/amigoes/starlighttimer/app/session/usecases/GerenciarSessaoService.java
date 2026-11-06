package dev.amigoes.starlighttimer.app.session.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.ObterConfiguracoesUsuarioUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.ObterPerfilUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.session.ports.in.AcaoSessao;
import dev.amigoes.starlighttimer.app.session.ports.in.GerenciarSessaoUseCase;
import dev.amigoes.starlighttimer.app.session.ports.out.SessaoRepositoryPort;
import dev.amigoes.starlighttimer.app.shared.ports.out.PublicadorEventosPort;
import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.entities.SessaoEvento;
import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;
import dev.amigoes.starlighttimer.domain.session.events.SessaoConcluida;
import dev.amigoes.starlighttimer.domain.session.exceptions.ConclusaoAntecipadaException;
import dev.amigoes.starlighttimer.domain.session.exceptions.SessaoAtivaExistenteException;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GerenciarSessaoService implements GerenciarSessaoUseCase {
    private final UsuarioAtualPort usuarioAtual;
    private final ObterConfiguracoesUsuarioUseCase configuracoes;
    private final ObterPerfilUseCase perfil;
    private final SessaoRepositoryPort sessoes;
    private final TransicoesSessaoService transicoes;
    private final PublicadorEventosPort eventos;

    public GerenciarSessaoService(UsuarioAtualPort usuarioAtual, ObterConfiguracoesUsuarioUseCase configuracoes,
                                 ObterPerfilUseCase perfil, SessaoRepositoryPort sessoes,
                                 TransicoesSessaoService transicoes, PublicadorEventosPort eventos) {
        this.usuarioAtual = usuarioAtual;
        this.configuracoes = configuracoes;
        this.perfil = perfil;
        this.sessoes = sessoes;
        this.transicoes = transicoes;
        this.eventos = eventos;
    }

    @Override
    @Transactional
    public Sessao iniciar(TipoSessao tipo, Integer duracaoPlanejada, OffsetDateTime reportadoEm) {
        UUID usuarioId = usuarioAtual.getUsuarioId();
        sessoes.buscarAtivaPorUsuario(usuarioId).ifPresent(ativa -> {
            throw new SessaoAtivaExistenteException(ativa.getId());
        });
        if (tipo == null || tipo == TipoSessao.LONG_BREAK) {
            throw new IllegalArgumentException("Tipo de sessao invalido para o MVP");
        }
        var config = configuracoes.obterConfiguracoes();
        int duracao = duracaoPlanejada != null ? duracaoPlanejada
                : tipo == TipoSessao.FOCUS ? config.getFocoEmSegundos() : config.getPausaEmSegundos();
        if (tipo == TipoSessao.FOCUS && (duracao < 900 || duracao > 5400 || duracao % 300 != 0)
                || tipo == TipoSessao.SHORT_BREAK && (duracao < 180 || duracao > 900)) {
            throw new IllegalArgumentException("Duracao planejada invalida");
        }
        var iniciada = transicoes.iniciar(usuarioId, tipo, duracao,
                ZoneId.of(perfil.obterPerfil().getFusoHorario()), reportadoEm);
        return sessoes.salvarComEvento(iniciada.sessao(), iniciada.evento());
    }

    @Override
    @Transactional(noRollbackFor = ConclusaoAntecipadaException.class)
    public ResultadoTransicao transicionar(UUID id, AcaoSessao acao, OffsetDateTime reportadoEm) {
        Sessao sessao = sessoes.buscarPorIdEUsuario(id, usuarioAtual.getUsuarioId())
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
        if (acao != AcaoSessao.CONCLUIR) return new ResultadoTransicao(sessao, null);
        SessaoConcluida concluida = publicarConclusao(sessao);
        return new ResultadoTransicao(sessao, concluida.recompensa());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sessao> buscarAtiva() {
        return sessoes.buscarAtivaPorUsuario(usuarioAtual.getUsuarioId());
    }

    @Override
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
        eventos.publicar(evento);
        return evento;
    }
}
