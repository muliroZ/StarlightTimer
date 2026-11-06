package dev.amigoes.starlighttimer.app.identity.ports.in;

import dev.amigoes.starlighttimer.domain.identity.entities.Perfil;

public interface AtualizarPerfilUseCase {
    Perfil atualizarPerfil(String nomeExibido, String avatar, String bio, String fusoHorario);
}