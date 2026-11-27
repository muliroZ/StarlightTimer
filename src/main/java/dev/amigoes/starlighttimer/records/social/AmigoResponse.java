package dev.amigoes.starlighttimer.records.social;

import dev.amigoes.starlighttimer.records.identity.ResumoPerfilPublico;

import java.util.UUID;

public record AmigoResponse(UUID id, String nomeExibido, String avatar) {
    public static AmigoResponse de(ResumoPerfilPublico perfil) {
        return new AmigoResponse(perfil.id(), perfil.nomeExibido(), perfil.avatar());
    }
}
