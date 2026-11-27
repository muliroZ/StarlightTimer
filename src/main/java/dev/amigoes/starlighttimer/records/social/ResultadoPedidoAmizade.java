package dev.amigoes.starlighttimer.records.social;

import dev.amigoes.starlighttimer.core.social.domain.PedidoAmizade;

public sealed interface ResultadoPedidoAmizade {
    record Aceito(Amizade amizade) implements ResultadoPedidoAmizade {
    }

    record Respondido(PedidoAmizade pedido) implements ResultadoPedidoAmizade {
    }
}
