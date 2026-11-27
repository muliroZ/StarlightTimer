package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.core.social.domain.PedidoAmizade;

public class PedidoAmizadeMapper {
    public static PedidoAmizadeEntity paraEntity(PedidoAmizade pedido) {
        return new PedidoAmizadeEntity(pedido.getId(), pedido.getRemetenteId(),
                pedido.getDestinatarioId(), pedido.getStatus(), pedido.getEnviadoEm(), pedido.getRespondidoEm());
    }

    public static PedidoAmizade paraDominio(PedidoAmizadeEntity entity) {
        return new PedidoAmizade(entity.getId(), entity.getRemetenteId(),
                entity.getDestinatarioId(), entity.getStatus(), entity.getEnviadoEm(), entity.getRespondidoEm());
    }
}
