package dev.amigoes.starlighttimer.events.kafka.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ConstelacaoDesbloqueadaPayloadV1(
        @JsonProperty("constelacao_id") UUID constelacaoId
) {}
