package dev.amigoes.starlighttimer.app.progression.ports.out;

import dev.amigoes.starlighttimer.domain.progression.entities.ProgressoInsignia;
import java.util.Optional;
import java.util.UUID;

public interface RegistrarProgressoInsigniaPort {
    Optional<ProgressoInsignia> buscarProgresso(UUID usuarioId, UUID insigniaId);
    void registrarProgresso(ProgressoInsignia progresso);
}
