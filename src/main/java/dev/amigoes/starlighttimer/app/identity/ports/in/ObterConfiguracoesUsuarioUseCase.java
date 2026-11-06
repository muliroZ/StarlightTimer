package dev.amigoes.starlighttimer.app.identity.ports.in;

import dev.amigoes.starlighttimer.domain.social.entities.ConfiguracoesUsuario;

public interface ObterConfiguracoesUsuarioUseCase {
    ConfiguracoesUsuario obterConfiguracoes();
}