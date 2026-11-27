package dev.amigoes.starlighttimer.records.identity;

import dev.amigoes.starlighttimer.core.identity.domain.ConfiguracoesUsuario;

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
