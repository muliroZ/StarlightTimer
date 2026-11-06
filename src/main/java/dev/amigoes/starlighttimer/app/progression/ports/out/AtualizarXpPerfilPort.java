package dev.amigoes.starlighttimer.app.progression.ports.out;

import java.util.UUID;

public interface AtualizarXpPerfilPort {
    int bloquearEObterTotal(UUID usuarioId);
    void adicionar(UUID usuarioId, int quantidade);
}
