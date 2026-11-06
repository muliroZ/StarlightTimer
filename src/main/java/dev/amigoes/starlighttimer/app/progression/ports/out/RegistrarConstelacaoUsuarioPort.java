package dev.amigoes.starlighttimer.app.progression.ports.out;

import java.time.Instant;
import java.util.UUID;

public interface RegistrarConstelacaoUsuarioPort {
    /** Retorna true apenas quando o desbloqueio foi inserido agora. */
    boolean registrarDesbloqueio(UUID constelacaoId, UUID usuarioId, Instant desbloqueadoEm);
}
