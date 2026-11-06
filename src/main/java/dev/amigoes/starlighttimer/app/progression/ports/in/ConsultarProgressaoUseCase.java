package dev.amigoes.starlighttimer.app.progression.ports.in;

import dev.amigoes.starlighttimer.app.progression.dto.ResumoProgressao;

import java.util.UUID;

public interface ConsultarProgressaoUseCase {
    ResumoProgressao consultar(UUID usuarioId);
}
