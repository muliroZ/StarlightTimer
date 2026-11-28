package dev.amigoes.starlighttimer.events.kafka.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PedidoAmizadeEnviadoPayloadV1(
        @JsonProperty("remetente_id") UUID remetenteId,
        @JsonProperty("destinatario_id") UUID destinatarioId
) {}
