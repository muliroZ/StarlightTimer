package dev.amigoes.starlighttimer.adapters.session.out.persistence;

import dev.amigoes.starlighttimer.domain.session.enums.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoJpaRepository extends JpaRepository<SessaoEntity, UUID> {

  Optional<SessaoEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);

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
  List<SessaoEntity> findHistoricoFiltrado(@Param("usuarioId") UUID usuarioId,
                                           @Param("dataInicio") LocalDate dataInicio,
                                           @Param("dataFim") LocalDate dataFim,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);

  List<SessaoEntity> findByStatusIn(Collection<StatusSessao> statuses);

  @Query("SELECT DISTINCT s.diaLocal FROM SessaoEntity s " +
      "WHERE s.usuarioId = :usuarioId " +
      "AND s.tipo = dev.amigoes.starlighttimer.domain.session.enums.TipoSessao.FOCUS " +
      "AND s.status = dev.amigoes.starlighttimer.domain.session.enums.StatusSessao.COMPLETED " +
      "AND s.diaLocal BETWEEN :dataInicio AND :dataFim " +
      "ORDER BY s.diaLocal ASC")
  List<LocalDate> findDiasComFocoConcluido(@Param("usuarioId") UUID usuarioId,
                                           @Param("dataInicio") LocalDate dataInicio,
                                           @Param("dataFim") LocalDate dataFim);
}
