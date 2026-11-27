package dev.amigoes.starlighttimer.records.social;

import java.time.Instant;
import java.util.UUID;

public record AmizadeResponse(UUID id, UUID usuario1Id, UUID usuario2Id, Instant estabelecidaEm) {
    public static AmizadeResponse de(Amizade amizade) {
        return new AmizadeResponse(amizade.id(), amizade.usuario1Id(), amizade.usuario2Id(), amizade.estabelecidaEm());
    }
}
