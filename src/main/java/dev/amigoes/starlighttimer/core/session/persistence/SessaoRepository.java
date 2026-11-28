package dev.amigoes.starlighttimer.core.session.persistence;

import dev.amigoes.starlighttimer.core.session.domain.Sessao;
import dev.amigoes.starlighttimer.core.session.domain.SessaoEvento;
import dev.amigoes.starlighttimer.enums.session.StatusSessao;
import dev.amigoes.starlighttimer.enums.session.TipoEvento;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SessaoRepository extends JpaRepository<SessaoEntity, UUID>, EventosSessao {

  Optional<SessaoEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM SessaoEntity s WHERE s.id = :id AND s.usuarioId = :usuarioId")
  Optional<SessaoEntity> findParaAtualizarPorIdEUsuario(@Param("id") UUID id, @Param("usuarioId") UUID usuarioId);

  Optional<SessaoEntity> findFirstByUsuarioIdAndStatusIn(UUID usuarioId, Collection<StatusSessao> statuses);

  @Query(value = "SELECT * FROM sessao WHERE usuario_id = :usuarioId " +
      "ORDER BY iniciado_em DESC, id DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
  List<SessaoEntity> findHistoricoByUsuarioId(@Param("usuarioId") UUID usuarioId,
                                               @Param("limit") int limit,
                                               @Param("offset") int offset);

  @Query(value = "SELECT * FROM sessao WHERE usuario_id = :usuarioId " +
      "AND (CAST(:dataInicio AS date) IS NULL OR dia_local >= CAST(:dataInicio AS date)) " +
      "AND (CAST(:dataFim AS date) IS NULL OR dia_local <= CAST(:dataFim AS date)) " +
      "ORDER BY iniciado_em DESC, id DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
  List<SessaoEntity> findHistoricoFiltrado(
          @Param("usuarioId") UUID usuarioId,
          @Param("dataInicio") LocalDate dataInicio,
          @Param("dataFim") LocalDate dataFim,
          @Param("limit") int limit,
          @Param("offset") int offset
  );

  @Query("SELECT s.id FROM SessaoEntity s WHERE s.status IN (:statuses) ORDER BY s.id")
  List<UUID> findIdsPorStatus(@Param("statuses") Collection<StatusSessao> statuses);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM SessaoEntity s WHERE s.id = :id AND s.status IN (:statuses)")
  Optional<SessaoEntity> findParaAtualizarPorIdEStatus(
          @Param("id") UUID id,
          @Param("statuses") Collection<StatusSessao> statuses
  );

  @Query("SELECT DISTINCT s.diaLocal FROM SessaoEntity s " +
      "WHERE s.usuarioId = :usuarioId " +
      "AND s.tipo = dev.amigoes.starlighttimer.enums.session.TipoSessao.FOCUS " +
      "AND s.status = dev.amigoes.starlighttimer.enums.session.StatusSessao.COMPLETED " +
      "AND s.diaLocal BETWEEN :dataInicio AND :dataFim " +
      "ORDER BY s.diaLocal ASC")
  List<LocalDate> listarDiasComFocoConcluido(
          @Param("usuarioId") UUID usuarioId,
          @Param("dataInicio") LocalDate dataInicio,
          @Param("dataFim") LocalDate dataFim
  );

  @Transactional
  default Sessao salvarComEvento(Sessao sessao, SessaoEvento evento) {
    Objects.requireNonNull(sessao, "sessao e obrigatoria");
    Objects.requireNonNull(evento, "evento e obrigatorio");
    if (!sessao.getId().equals(evento.sessaoId())) {
      throw new IllegalArgumentException("evento pertence a outra sessao");
    }
    SessaoEntity sessaoEntity = SessaoMapper.paraEntidade(sessao);
    save(sessaoEntity);

    SessaoEventoEntity eventoEntity = SessaoMapper.paraEntidade(evento);
    salvarEvento(eventoEntity);

    return sessao;
  }

  default Optional<Sessao> buscarPorIdEUsuario(UUID id, UUID usuarioId) {
    return findByIdAndUsuarioId(id, usuarioId)
        .map(this::reconstituirSessao);
  }

  default Optional<Sessao> buscarPorIdEUsuarioParaAtualizar(UUID id, UUID usuarioId) {
    return findParaAtualizarPorIdEUsuario(id, usuarioId).map(this::reconstituirSessao);
  }

  default Optional<Sessao> buscarAtivaPorUsuario(UUID usuarioId) {
    return findFirstByUsuarioIdAndStatusIn(usuarioId, Set.of(StatusSessao.RUNNING, StatusSessao.PAUSED))
        .map(this::reconstituirSessao);
  }

  default Optional<SessaoEvento> buscarUltimoEventoPorTipo(UUID sessaoId, TipoEvento tipoEvento) {
    return buscarUltimoEvento(sessaoId, tipoEvento)
        .map(SessaoMapper::paraDominio);
  }

  default List<Sessao> listarHistorico(UUID usuarioId, int limit, int offset) {
    if (limit < 1 || offset < 0) {
      throw new IllegalArgumentException("limit deve ser positivo e offset nao pode ser negativo");
    }
    return findHistoricoByUsuarioId(usuarioId, limit, offset)
        .stream()
        .map(this::reconstituirSessao)
        .toList();
  }

  default List<Sessao> listarHistorico(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim, int limit, int offset) {
    if (limit < 1 || offset < 0) {
      throw new IllegalArgumentException("limit deve ser positivo e offset nao pode ser negativo");
    }
    return findHistoricoFiltrado(usuarioId, dataInicio, dataFim, limit, offset)
        .stream().map(this::reconstituirSessao).toList();
  }

  default List<UUID> listarIdsAtivos() {
    return findIdsPorStatus(Set.of(StatusSessao.RUNNING, StatusSessao.PAUSED));
  }

  default Optional<Sessao> buscarAtivaParaAtualizar(UUID id) {
    return findParaAtualizarPorIdEStatus(id, Set.of(StatusSessao.RUNNING, StatusSessao.PAUSED))
        .map(this::reconstituirSessao);
  }

  private Sessao reconstituirSessao(SessaoEntity entity) {
    SessaoEvento ultimoEventoPausa = null;
    if (entity.getStatus() == StatusSessao.PAUSED) {
      ultimoEventoPausa = buscarUltimoEvento(entity.getId(), TipoEvento.PAUSADA)
          .map(SessaoMapper::paraDominio)
          .orElse(null);
    }
    return SessaoMapper.paraDominio(entity, ultimoEventoPausa);
  }
}
