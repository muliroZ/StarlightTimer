package dev.amigoes.starlighttimer.app.identity.ports.out;

import dev.amigoes.starlighttimer.domain.social.entities.ConfiguracoesUsuario;

import java.util.Optional;
import java.util.UUID;

public interface ConfiguracoesUsuarioRepositoryPort {
    ConfiguracoesUsuario salvar(ConfiguracoesUsuario configuracoes);
    Optional<ConfiguracoesUsuario> buscarPorUsuarioId(UUID usuarioId);
}