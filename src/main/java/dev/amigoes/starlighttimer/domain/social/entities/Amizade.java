package dev.amigoes.starlighttimer.domain.social.entities;

import java.time.Instant;
import java.util.UUID;

public record Amizade(
    UUID id,
    UUID usuario1Id,
    UUID usuario2Id,
    Instant estabelecidaEm
) {
}