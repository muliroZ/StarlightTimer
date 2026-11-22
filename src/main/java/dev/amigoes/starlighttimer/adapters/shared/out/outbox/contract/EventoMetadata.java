package dev.amigoes.starlighttimer.adapters.shared.out.outbox.contract;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public record EventoMetadata(
    UUID id,
    TipoEvento tipoEvento,
    TipoAgregado tipoAgregado,
    UUID agregadoId,
    Instant criadoEm,
    int versaoPayload
) {
    public static EventoMetadata gerar(
            TipoEvento tipoEvento,
            UUID agregadoId,
            Clock clock,
            int versaoPayload
    ) {
        return new EventoMetadata(
                UUID.randomUUID(),
                tipoEvento,
                tipoEvento.derivarAgregado(),
                agregadoId,
                clock.instant(),
                versaoPayload
        );
    }
}
