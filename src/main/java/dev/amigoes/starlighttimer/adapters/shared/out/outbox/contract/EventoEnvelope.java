package dev.amigoes.starlighttimer.adapters.shared.out.outbox.contract;

public record EventoEnvelope<T>(
        EventoMetadata metadata,
        T payload
) {}
