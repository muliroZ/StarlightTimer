package dev.amigoes.starlighttimer.adapters.social.in.web;

import dev.amigoes.starlighttimer.app.identity.ports.in.ResumoPerfilPublico;

import java.util.UUID;

public record AmigoResponse(UUID id, String nomeExibido, String avatar) {
    public static AmigoResponse de(ResumoPerfilPublico perfil) {
        return new AmigoResponse(perfil.id(), perfil.nomeExibido(), perfil.avatar());
    }
}
