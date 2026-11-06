package dev.amigoes.starlighttimer.app.progression.ports.out;

import java.util.UUID;

public interface AtualizarXpPerfilPort {
    /** Serializa concessões concorrentes do usuário e devolve o cache atual. */
    int bloquearEObterTotal(UUID usuarioId);
    void adicionar(UUID usuarioId, int quantidade);
}
