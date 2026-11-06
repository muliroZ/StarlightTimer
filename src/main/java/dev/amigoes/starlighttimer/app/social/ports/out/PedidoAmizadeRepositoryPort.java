package dev.amigoes.starlighttimer.app.social.ports.out;

import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;

import java.util.Optional;
import java.util.UUID;

public interface PedidoAmizadeRepositoryPort {
    /** Serializa operacoes do mesmo par enquanto a transacao estiver ativa. */
    void bloquearPar(UUID primeiro, UUID segundo);

    Optional<PedidoAmizade> buscarPorId(UUID id);

    void criar(PedidoAmizade pedido);

    /**
     * Altera um pedido pendente e devolve se a linha foi atualizada.
     * Em ACEITO, o trigger da V5 insere a amizade e exclui o pedido na mesma transacao.
     * O chamador consulta a amizade apos o aceite.
     */
    boolean atualizarPendente(PedidoAmizade pedido);
}
