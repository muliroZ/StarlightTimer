package dev.amigoes.starlighttimer.adapters.session.out.persistence;

import dev.amigoes.starlighttimer.app.session.ports.out.SessaoRepositoryPort;
import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.entities.SessaoEvento;
import dev.amigoes.starlighttimer.domain.session.enums.StatusSessao;
import dev.amigoes.starlighttimer.domain.session.enums.TipoEvento;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class SessaoPersistenceAdapter implements SessaoRepositoryPort {

  private final SessaoJpaRepository sessaoRepository;
  private final SessaoEventoJpaRepository eventoRepository;

  public SessaoPersistenceAdapter(SessaoJpaRepository sessaoRepository,
                                  SessaoEventoJpaRepository eventoRepository) {
    this.sessaoRepository = Objects.requireNonNull(sessaoRepository);
    this.eventoRepository = Objects.requireNonNull(eventoRepository);
  }

  @Override
  @Transactional
  public Sessao salvarComEvento(Sessao sessao, SessaoEvento evento) {
    Objects.requireNonNull(sessao, "sessao e obrigatoria");
    Objects.requireNonNull(evento, "evento e obrigatorio");
    if (!sessao.getId().equals(evento.sessaoId())) {
      throw new IllegalArgumentException("evento pertence a outra sessao");
    }
    SessaoEntity sessaoEntity = SessaoMapper.paraEntidade(sessao);
    sessaoRepository.save(sessaoEntity);

    SessaoEventoEntity eventoEntity = SessaoMapper.paraEntidade(evento);
    eventoRepository.save(eventoEntity);

    return sessao;
  }

  @Override
  public Optional<Sessao> buscarPorIdEUsuario(UUID id, UUID usuarioId) {
    return sessaoRepository.findByIdAndUsuarioId(id, usuarioId)
        .map(this::reconstituirSessao);
  }

  @Override
  public Optional<Sessao> buscarAtivaPorUsuario(UUID usuarioId) {
    return sessaoRepository.findFirstByUsuarioIdAndStatusIn(usuarioId, Set.of(StatusSessao.RUNNING, StatusSessao.PAUSED))
        .map(this::reconstituirSessao);
  }

  @Override
  public Optional<SessaoEvento> buscarUltimoEventoPorTipo(UUID sessaoId, TipoEvento tipoEvento) {
    return eventoRepository.findFirstBySessaoIdAndTipoEventoOrderByOcorridoEmDesc(sessaoId, tipoEvento)
        .map(SessaoMapper::paraDominio);
  }

  @Override
  public List<Sessao> listarHistorico(UUID usuarioId, int limit, int offset) {
    if (limit < 1 || offset < 0) {
      throw new IllegalArgumentException("limit deve ser positivo e offset nao pode ser negativo");
    }
    return sessaoRepository.findHistoricoByUsuarioId(usuarioId, limit, offset)
        .stream()
        .map(this::reconstituirSessao)
        .toList();
  }

  @Override
  public List<Sessao> listarHistorico(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim, int limit, int offset) {
    if (limit < 1 || offset < 0) {
      throw new IllegalArgumentException("limit deve ser positivo e offset nao pode ser negativo");
    }
    return sessaoRepository.findHistoricoFiltrado(usuarioId, dataInicio, dataFim, limit, offset)
        .stream().map(this::reconstituirSessao).toList();
  }

  @Override
  public List<Sessao> listarAtivas() {
    return sessaoRepository.findByStatusIn(Set.of(StatusSessao.RUNNING, StatusSessao.PAUSED))
        .stream().map(this::reconstituirSessao).toList();
  }

  @Override
  public List<LocalDate> listarDiasComFocoConcluido(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
    return sessaoRepository.findDiasComFocoConcluido(usuarioId, dataInicio, dataFim);
  }

  private Sessao reconstituirSessao(SessaoEntity entity) {
    SessaoEvento ultimoEventoPausa = null;
    if (entity.getStatus() == StatusSessao.PAUSED) {
      ultimoEventoPausa = eventoRepository
          .findFirstBySessaoIdAndTipoEventoOrderByOcorridoEmDesc(entity.getId(), TipoEvento.PAUSADA)
          .map(SessaoMapper::paraDominio)
          .orElse(null);
    }
    return SessaoMapper.paraDominio(entity, ultimoEventoPausa);
  }
}
