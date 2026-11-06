package dev.amigoes.starlighttimer.app.identity.ports.out;

import dev.amigoes.starlighttimer.domain.identity.entities.Perfil;

import java.util.Optional;
import java.util.UUID;

public interface PerfilRepositoryPort {
    Perfil salvar(Perfil perfil);
    Optional<Perfil> buscarPorUsuarioId(UUID usuarioId);
}