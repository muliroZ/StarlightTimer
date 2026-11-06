package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;
import org.springframework.stereotype.Component;

@Component
public class PedidoAmizadeMapper {
    public PedidoAmizadeEntity paraEntity(PedidoAmizade pedido) {
        return new PedidoAmizadeEntity(pedido.getId(), pedido.getRemetenteId(),
                pedido.getDestinatarioId(), pedido.getStatus(), pedido.getEnviadoEm(), pedido.getRespondidoEm());
    }

    public PedidoAmizade paraDominio(PedidoAmizadeEntity entity) {
        return new PedidoAmizade(entity.getId(), entity.getRemetenteId(),
                entity.getDestinatarioId(), entity.getStatus(), entity.getEnviadoEm(), entity.getRespondidoEm());
    }
}
