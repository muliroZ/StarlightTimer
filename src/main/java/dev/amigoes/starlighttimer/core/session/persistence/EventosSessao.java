package dev.amigoes.starlighttimer.core.session.persistence;

import dev.amigoes.starlighttimer.enums.session.TipoEvento;

import java.util.Optional;
import java.util.UUID;

interface EventosSessao {
    void salvarEvento(SessaoEventoEntity evento);

    Optional<SessaoEventoEntity> buscarUltimoEvento(UUID sessaoId, TipoEvento tipo);
}
