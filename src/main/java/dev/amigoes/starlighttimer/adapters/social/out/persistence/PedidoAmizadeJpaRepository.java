package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.domain.social.enums.StatusPedidoAmizade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PedidoAmizadeJpaRepository extends JpaRepository<PedidoAmizadeEntity, UUID> {
    List<PedidoAmizadeEntity> findByDestinatarioIdAndStatusOrderByEnviadoEmDesc(UUID destinatarioId, StatusPedidoAmizade status);

    List<PedidoAmizadeEntity> findByRemetenteIdAndStatusOrderByEnviadoEmDesc(UUID remetenteId, StatusPedidoAmizade status);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update PedidoAmizadeEntity p set p.status = :cancelado, p.respondidoEm = :respondidoEm
            where p.status = :pendente and
            ((p.remetenteId = :primeiro and p.destinatarioId = :segundo)
              or (p.remetenteId = :segundo and p.destinatarioId = :primeiro))
            """)
    int cancelarPendentesEntre(@Param("primeiro") UUID primeiro, @Param("segundo") UUID segundo,
                               @Param("pendente") StatusPedidoAmizade pendente,
                               @Param("cancelado") StatusPedidoAmizade cancelado,
                               @Param("respondidoEm") Instant respondidoEm);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update PedidoAmizadeEntity p set p.status = :aceito
            where p.id = :id and p.status = :pendente
            """)
    int aceitarPendente(@Param("id") UUID id,
                       @Param("pendente") StatusPedidoAmizade pendente,
                       @Param("aceito") StatusPedidoAmizade aceito);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update PedidoAmizadeEntity p set p.status = :status, p.respondidoEm = :respondidoEm
            where p.id = :id and p.status = :pendente
            """)
    int responderPendente(@Param("id") UUID id,
                         @Param("pendente") StatusPedidoAmizade pendente,
                         @Param("status") StatusPedidoAmizade status,
                         @Param("respondidoEm") Instant respondidoEm);
}
