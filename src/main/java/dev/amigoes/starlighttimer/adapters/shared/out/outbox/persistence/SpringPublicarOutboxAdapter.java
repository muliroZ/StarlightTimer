package dev.amigoes.starlighttimer.adapters.shared.out.outbox.persistence;

import dev.amigoes.starlighttimer.app.shared.ports.out.PublicarOutboxPort;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

@Slf4j
@Component
public class SpringPublicarOutboxAdapter implements PublicarOutboxPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final Clock clock;

    public SpringPublicarOutboxAdapter(
            KafkaTemplate<String, String> kafkaTemplate,
            OutboxRepository outboxRepository,
            Clock clock
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
        this.clock = clock;
    }

    @Override
    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publicarEventosPendentes() {
        List<OutboxEntity> eventosPendentes = outboxRepository.findTop100ByPublicadoEmNullOrderByCriadoEmAsc();

        for (OutboxEntity evento : eventosPendentes) {
            try {
                kafkaTemplate.send(
                        evento.getTipoAgregado().definirTopico(),
                        evento.getAgregadoId().toString(),
                        evento.getPayload()
                ).get();

                evento.marcarDataPublicacao(clock);
                outboxRepository.save(evento);
            } catch (Exception e) {
                log.error(
                        "Falha ao publicar o evento outbox de ID {} no tópico {}",
                        evento.getId(), evento.getTipoAgregado().definirTopico()
                );
            }
        }
    }
}
