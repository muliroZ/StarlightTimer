package dev.amigoes.starlighttimer.adapters.identity.in.web;

import dev.amigoes.starlighttimer.domain.social.entities.ConfiguracoesUsuario;

import java.util.UUID;

public record ConfiguracoesUsuarioResponse(
        UUID usuarioId,
        int focoEmSegundos,
        int pausaEmSegundos,
        boolean iniciarPausaAuto,
        boolean somHabilitado
) {
    public static ConfiguracoesUsuarioResponse deDominio(ConfiguracoesUsuario config) {
        return new ConfiguracoesUsuarioResponse(
                config.getUsuarioId(),
                config.getFocoEmSegundos(),
                config.getPausaEmSegundos(),
                config.isIniciarPausaAuto(),
                config.isSomHabilitado()
        );
    }
}