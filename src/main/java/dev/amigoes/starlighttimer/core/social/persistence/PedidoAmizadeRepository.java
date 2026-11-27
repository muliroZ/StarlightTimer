package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.core.social.domain.PedidoAmizade;
import dev.amigoes.starlighttimer.enums.social.StatusPedidoAmizade;
import dev.amigoes.starlighttimer.exceptions.ConflitoException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
public interface PedidoAmizadeRepository extends JpaRepository<PedidoAmizadeEntity, UUID>, OperacoesPedidoAmizade {
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

    default List<PedidoAmizade> listarPendentesRecebidos(UUID usuarioId) {
        return findByDestinatarioIdAndStatusOrderByEnviadoEmDesc(usuarioId, StatusPedidoAmizade.PENDENTE)
                .stream().map(PedidoAmizadeMapper::paraDominio).toList();
    }

    default List<PedidoAmizade> listarPendentesEnviados(UUID usuarioId) {
        return findByRemetenteIdAndStatusOrderByEnviadoEmDesc(usuarioId, StatusPedidoAmizade.PENDENTE)
                .stream().map(PedidoAmizadeMapper::paraDominio).toList();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    default void cancelarPendentesEntre(UUID primeiro, UUID segundo, Instant respondidoEm) {
        cancelarPendentesEntre(primeiro, segundo, StatusPedidoAmizade.PENDENTE,
                StatusPedidoAmizade.CANCELADO, respondidoEm);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    default boolean atualizarPendente(PedidoAmizade pedido) {
        var status = pedido.getStatus();
        if (status == StatusPedidoAmizade.PENDENTE) {
            throw new IllegalArgumentException("A resposta precisa alterar o estado do pedido");
        }
        try {
            if (status == StatusPedidoAmizade.ACEITO) {
                // A V5 cria a amizade e apaga o pedido dentro deste UPDATE.
                return aceitarPendente(pedido.getId(), StatusPedidoAmizade.PENDENTE, status) == 1;
            }
            return responderPendente(pedido.getId(), StatusPedidoAmizade.PENDENTE,
                    status, pedido.getRespondidoEm()) == 1;
        } catch (DataIntegrityViolationException ex) {
            if (violouUnicidade(ex, "par_unico")) {
                throw new ConflitoException("A amizade ja existe");
            }
            throw ex;
        }
    }

    private boolean violouUnicidade(Throwable erro, String restricao) {
        for (Throwable causa = erro; causa != null; causa = causa.getCause()) {
            if (causa instanceof ConstraintViolationException violacao
                    && "23505".equals(violacao.getSQLState())
                    && restricao.equals(violacao.getConstraintName())) {
                return true;
            }
        }
        return false;
    }
}
