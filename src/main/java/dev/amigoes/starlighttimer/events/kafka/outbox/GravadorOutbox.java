package dev.amigoes.starlighttimer.events.kafka.outbox;

import dev.amigoes.starlighttimer.core.interfaces.GravadorEventos;
import dev.amigoes.starlighttimer.events.kafka.EventoEnvelope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.ZoneOffset;

@Component
public class GravadorOutbox implements GravadorEventos {

    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;

    public GravadorOutbox(OutboxRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void gravar(EventoEnvelope<?> evento) {
        String payloadJson = serializarEvento(evento);

        OutboxEntity outbox = new OutboxEntity(
                evento.metadata().id(),
                evento.metadata().tipoAgregado(),
                evento.metadata().agregadoId(),
                evento.metadata().tipoEvento(),
                payloadJson,
                evento.metadata().criadoEm().atOffset(ZoneOffset.UTC)
        );

        repository.save(outbox);
    }

    private String serializarEvento(EventoEnvelope<?> evento) {
        try {
            return objectMapper.writeValueAsString(evento);
        } catch (JacksonException e) {
            throw new IllegalStateException("Erro ao serializar evento da outbox", e);
        }
    }
}
