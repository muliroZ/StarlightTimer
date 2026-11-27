package dev.amigoes.starlighttimer.events.kafka;

public record EventoEnvelope<T>(
        EventoMetadata metadata,
        T payload
) {}
