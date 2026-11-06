package dev.amigoes.starlighttimer.app.progression.ports.out;

import dev.amigoes.starlighttimer.app.progression.dto.HistoricoProgressao;
import java.util.UUID;

public interface LerHistoricoProgressaoPort {
    HistoricoProgressao carregar(UUID usuarioId);
}
