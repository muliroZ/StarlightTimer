package dev.amigoes.starlighttimer.core.interfaces;

import dev.amigoes.starlighttimer.events.kafka.EventoEnvelope;

public interface GravadorEventos {
    void gravar(EventoEnvelope<?> evento);
}
