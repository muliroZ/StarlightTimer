package dev.amigoes.starlighttimer.domain.social.entities;

import java.util.UUID;

public record Bloqueio(UUID usuarioId, UUID bloqueadoId) {
}