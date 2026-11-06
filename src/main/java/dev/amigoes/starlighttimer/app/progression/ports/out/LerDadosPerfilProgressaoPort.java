package dev.amigoes.starlighttimer.app.progression.ports.out;

import dev.amigoes.starlighttimer.app.progression.dto.DadosPerfilProgressao;

import java.util.Optional;
import java.util.UUID;

public interface LerDadosPerfilProgressaoPort {
    Optional<DadosPerfilProgressao> carregar(UUID usuarioId);
}
