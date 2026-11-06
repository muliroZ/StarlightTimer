package dev.amigoes.starlighttimer.adapters.session.out.persistence;

import dev.amigoes.starlighttimer.app.session.ports.out.SessaoRepositoryPort;
import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.entities.SessaoEvento;
import dev.amigoes.starlighttimer.domain.session.enums.StatusSessao;
import dev.amigoes.starlighttimer.domain.session.enums.TipoEvento;
import org.springframework.data.domain.PageRequest;
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
    SessaoEntity sessaoEntity = SessaoMapper.paraEntidade(sessao);
    sessaoRepository.save(sessaoEntity);

    if (evento != null) {
      SessaoEventoEntity eventoEntity = SessaoMapper.paraEntidade(evento);
      eventoRepository.save(eventoEntity);
    }

    return sessao;
  }

  @Override
  @Transactional
  public Sessao salvar(Sessao sessao) {
    SessaoEntity sessaoEntity = SessaoMapper.paraEntidade(sessao);
    sessaoRepository.save(sessaoEntity);
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
    int page = offset / Math.max(1, limit);
    PageRequest pageRequest = PageRequest.of(page, limit);
    return sessaoRepository.findHistoricoByUsuarioId(usuarioId, pageRequest)
        .stream()
        .map(this::reconstituirSessao)
        .toList();
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