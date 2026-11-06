package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.app.social.ports.out.PedidoAmizadeRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.ConflitoException;
import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;
import dev.amigoes.starlighttimer.domain.social.enums.StatusPedidoAmizade;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.time.Instant;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class PedidoAmizadePersistenceAdapter implements PedidoAmizadeRepositoryPort {
    private final PedidoAmizadeJpaRepository pedidos;
    private final PedidoAmizadeMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    public PedidoAmizadePersistenceAdapter(PedidoAmizadeJpaRepository pedidos, PedidoAmizadeMapper mapper) {
        this.pedidos = pedidos;
        this.mapper = mapper;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
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
        return pedidos.findById(id).map(entity -> {
            var pedido = mapper.paraDominio(entity);
            entityManager.detach(entity);
            return pedido;
        });
    }

    @Override
    public List<PedidoAmizade> listarPendentesRecebidos(UUID usuarioId) {
        return pedidos.findByDestinatarioIdAndStatusOrderByEnviadoEmDesc(usuarioId, StatusPedidoAmizade.PENDENTE)
                .stream().map(mapper::paraDominio).toList();
    }

    @Override
    public List<PedidoAmizade> listarPendentesEnviados(UUID usuarioId) {
        return pedidos.findByRemetenteIdAndStatusOrderByEnviadoEmDesc(usuarioId, StatusPedidoAmizade.PENDENTE)
                .stream().map(mapper::paraDominio).toList();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void cancelarPendentesEntre(UUID primeiro, UUID segundo, Instant respondidoEm) {
        pedidos.cancelarPendentesEntre(primeiro, segundo, StatusPedidoAmizade.PENDENTE,
                StatusPedidoAmizade.CANCELADO, respondidoEm);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void criar(PedidoAmizade pedido) {
        try {
            entityManager.persist(mapper.paraEntity(pedido));
            entityManager.flush();
        } catch (PersistenceException ex) {
            if (violouUnicidade(ex, "uq_remetente_destinatario")) {
                throw new ConflitoException("Ja existe um pedido pendente entre esses usuarios");
            }
            throw ex;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean atualizarPendente(PedidoAmizade pedido) {
        var status = pedido.getStatus();
        if (status == StatusPedidoAmizade.PENDENTE) {
            throw new IllegalArgumentException("A resposta precisa alterar o estado do pedido");
        }
        try {
            if (status == StatusPedidoAmizade.ACEITO) {
                // A V5 cria a amizade e apaga o pedido dentro deste UPDATE.
                return pedidos.aceitarPendente(pedido.getId(), StatusPedidoAmizade.PENDENTE, status) == 1;
            }
            return pedidos.responderPendente(pedido.getId(), StatusPedidoAmizade.PENDENTE,
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
