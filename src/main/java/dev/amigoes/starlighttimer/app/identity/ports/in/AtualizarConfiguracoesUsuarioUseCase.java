package dev.amigoes.starlighttimer.app.identity.ports.in;

import dev.amigoes.starlighttimer.domain.identity.entities.ConfiguracoesUsuario;

public interface AtualizarConfiguracoesUsuarioUseCase {
    ConfiguracoesUsuario atualizarConfiguracoes(Integer focoEmSegundos, Integer pausaEmSegundos, Boolean iniciarPausaAuto, Boolean somHabilitado);
}