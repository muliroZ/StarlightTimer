package dev.amigoes.starlighttimer.records.identity;

import dev.amigoes.starlighttimer.core.identity.domain.Perfil;

import java.util.UUID;

public record PerfilResponse(
        UUID usuarioId,
        String nomeExibido,
        String avatar,
        String bio,
        String fusoHorario,
        int xpTotal
) {
    public static PerfilResponse deDominio(Perfil perfil) {
        return new PerfilResponse(
                perfil.getUsuarioId(),
                perfil.getNomeExibido(),
                perfil.getAvatar(),
                perfil.getBio(),
                perfil.getFusoHorario(),
                perfil.getXpTotal()
        );
    }
}
