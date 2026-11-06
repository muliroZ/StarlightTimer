package dev.amigoes.starlighttimer.adapters.social.in.web;

import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;
import dev.amigoes.starlighttimer.domain.social.enums.StatusPedidoAmizade;

import java.time.Instant;
import java.util.UUID;

public record PedidoAmizadeResponse(UUID id, UUID remetenteId, UUID destinatarioId,
                                    StatusPedidoAmizade status, Instant enviadoEm, Instant respondidoEm) {
    public static PedidoAmizadeResponse de(PedidoAmizade pedido) {
        return new PedidoAmizadeResponse(pedido.getId(), pedido.getRemetenteId(),
                pedido.getDestinatarioId(), pedido.getStatus(), pedido.getEnviadoEm(), pedido.getRespondidoEm());
    }
}
