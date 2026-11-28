package dev.amigoes.starlighttimer.events.kafka.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record InsigniaDesbloqueadaPayloadV1(
        @JsonProperty("insignia_id") UUID insigniaId
) {}
