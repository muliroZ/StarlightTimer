package dev.amigoes.starlighttimer.domain.session.enums;

import dev.amigoes.starlighttimer.domain.session.exceptions.TransicaoInvalidaException;

import java.util.Map;
import java.util.Set;

public enum StatusSessao {
    RUNNING,
    PAUSED,
    COMPLETED,
    ABANDONED;

    public StatusSessao changeStatus(StatusSessao next) {
        Map<StatusSessao, Set<StatusSessao>> transitions = Map.of(
                RUNNING,    Set.of(PAUSED, COMPLETED, ABANDONED),
                PAUSED,     Set.of(RUNNING, ABANDONED),
                COMPLETED,  Set.of(),
                ABANDONED,  Set.of()
        );

        if (!transitions.get(this).contains(next)) {
            throw new TransicaoInvalidaException("A sessão não pode sair de " + this.name() + " para " + next.name());
        }

        return next;
    }
}
