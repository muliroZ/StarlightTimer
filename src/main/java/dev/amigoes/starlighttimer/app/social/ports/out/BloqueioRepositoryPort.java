package dev.amigoes.starlighttimer.app.social.ports.out;

import dev.amigoes.starlighttimer.domain.social.entities.Bloqueio;

import java.util.List;
import java.util.UUID;

public interface BloqueioRepositoryPort {
    List<Bloqueio> buscarEntre(UUID primeiro, UUID segundo);
    void criar(UUID usuarioId, UUID bloqueadoId);
    void remover(UUID usuarioId, UUID bloqueadoId);
}
