package dev.amigoes.starlighttimer.records.social;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EnviarPedidoAmizadeRequest(@NotNull UUID destinatarioId) {
}
