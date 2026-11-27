package dev.amigoes.starlighttimer.records.social;

import dev.amigoes.starlighttimer.enums.social.AcaoPedidoAmizade;

import jakarta.validation.constraints.NotNull;

public record ResponderPedidoAmizadeRequest(@NotNull AcaoPedidoAmizade acao) {
}
