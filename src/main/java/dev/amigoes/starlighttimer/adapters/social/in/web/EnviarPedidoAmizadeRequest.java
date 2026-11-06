package dev.amigoes.starlighttimer.adapters.social.in.web;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EnviarPedidoAmizadeRequest(@NotNull UUID destinatarioId) {
}
