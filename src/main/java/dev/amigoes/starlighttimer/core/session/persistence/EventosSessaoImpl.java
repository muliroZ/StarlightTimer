package dev.amigoes.starlighttimer.core.session.persistence;

import dev.amigoes.starlighttimer.enums.session.TipoEvento;

import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

class EventosSessaoImpl implements EventosSessao {
    private final EntityManager entidades;

    EventosSessaoImpl(EntityManager entidades) {
        this.entidades = entidades;
    }

    @Override
    public void salvarEvento(SessaoEventoEntity evento) {
        entidades.merge(evento);
    }

    @Override
    public Optional<SessaoEventoEntity> buscarUltimoEvento(UUID sessaoId, TipoEvento tipo) {
        return entidades.createQuery("""
                        select e from SessaoEventoEntity e
                        where e.sessaoId = :sessaoId and e.tipoEvento = :tipo
                        order by e.ocorridoEm desc
                        """, SessaoEventoEntity.class)
                .setParameter("sessaoId", sessaoId)
                .setParameter("tipo", tipo)
                .setMaxResults(1)
                .getResultList().stream().findFirst();
    }
}
