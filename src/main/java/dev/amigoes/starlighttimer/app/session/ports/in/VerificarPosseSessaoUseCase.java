package dev.amigoes.starlighttimer.app.session.ports.in;

import java.util.UUID;

public interface VerificarPosseSessaoUseCase {
    void verificar(UUID usuarioId, UUID sessaoId);
}
