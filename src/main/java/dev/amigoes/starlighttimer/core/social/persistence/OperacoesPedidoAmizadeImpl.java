package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.core.social.domain.PedidoAmizade;
import dev.amigoes.starlighttimer.exceptions.ConflitoException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;
import java.util.UUID;

class OperacoesPedidoAmizadeImpl implements OperacoesPedidoAmizade {
    private final EntityManager entityManager;

    OperacoesPedidoAmizadeImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void bloquearPar(UUID primeiro, UUID segundo) {
        // A mesma chave e usada nos dois sentidos. O banco libera o lock no fim da transacao.
        entityManager.createNativeQuery("""
                select 1 from pg_advisory_xact_lock(hashtextextended(
                    concat('starlight:amizade:',
                        least(cast(:primeiro as uuid), cast(:segundo as uuid)), ':',
                        greatest(cast(:primeiro as uuid), cast(:segundo as uuid))), 0))
                """, Integer.class)
                .setParameter("primeiro", primeiro)
                .setParameter("segundo", segundo)
                .getSingleResult();
    }

    @Override
    public Optional<PedidoAmizade> buscarPorId(UUID id) {
        return Optional.ofNullable(entityManager.find(PedidoAmizadeEntity.class, id)).map(entity -> {
            var pedido = PedidoAmizadeMapper.paraDominio(entity);
            entityManager.detach(entity);
            return pedido;
        });
    }

    @Override
    public void criar(PedidoAmizade pedido) {
        try {
            entityManager.persist(PedidoAmizadeMapper.paraEntity(pedido));
            entityManager.flush();
        } catch (PersistenceException ex) {
            if (violouUnicidade(ex, "uq_remetente_destinatario")) {
                throw new ConflitoException("Ja existe um pedido pendente entre esses usuarios");
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
