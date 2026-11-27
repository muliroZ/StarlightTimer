package dev.amigoes.starlighttimer.events;

import dev.amigoes.starlighttimer.core.progression.usecase.ConcederProgressaoService;

import jakarta.persistence.EntityManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SessaoConcluidaListener {
    private final ConcederProgressaoService progressao;
    private final EntityManager entidades;

    public SessaoConcluidaListener(ConcederProgressaoService progressao, EntityManager entidades) {
        this.progressao = progressao;
        this.entidades = entidades;
    }

    @EventListener
    public void aoConcluir(SessaoConcluida evento) {
        entidades.flush();
        evento.registrarRecompensa(progressao.conceder(evento));
    }
}
