package dev.amigoes.starlighttimer.events.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public record EventoMetadata(
    UUID id,
    @JsonProperty("tipo_evento") TipoEvento tipoEvento,
    @JsonProperty("tipo_agregado") TipoAgregado tipoAgregado,
    @JsonProperty("agregado_id") UUID agregadoId,
    @JsonProperty("criado_em") Instant criadoEm,
    @JsonProperty("versao_payload") int versaoPayload
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
