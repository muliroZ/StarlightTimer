package dev.amigoes.starlighttimer.adapters.social.in.web;

import dev.amigoes.starlighttimer.app.social.ports.in.AcaoPedidoAmizade;
import jakarta.validation.constraints.NotNull;

public record ResponderPedidoAmizadeRequest(@NotNull AcaoPedidoAmizade acao) {
}
