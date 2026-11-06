package dev.amigoes.starlighttimer.adapters.identity.in.web;

import dev.amigoes.starlighttimer.domain.social.entities.Perfil;

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