package dev.amigoes.starlighttimer.adapters.out.eventos;

import dev.amigoes.starlighttimer.app.ports.out.PublicadorEventosPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringPublicadorEventosAdapter implements PublicadorEventosPort {

    private final ApplicationEventPublisher publisher;

    public SpringPublicadorEventosAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publicar(Object evento) {
        publisher.publishEvent(evento);
    }
}
