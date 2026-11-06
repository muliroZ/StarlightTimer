package dev.amigoes.starlighttimer.app.social.ports.in;

import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;

import java.util.UUID;

public interface GerenciarPedidosAmizadePort {
    PedidoAmizade enviar(UUID destinatarioId);

    ResultadoPedidoAmizade responder(UUID pedidoId, AcaoPedidoAmizade acao);
}
