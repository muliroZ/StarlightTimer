package dev.amigoes.starlighttimer.app.social.ports.in;

import dev.amigoes.starlighttimer.domain.social.entities.Amizade;
import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;

public sealed interface ResultadoPedidoAmizade {
    record Aceito(Amizade amizade) implements ResultadoPedidoAmizade {
    }

    record Respondido(PedidoAmizade pedido) implements ResultadoPedidoAmizade {
    }
}
