package dev.amigoes.starlighttimer.core.identity.persistence;

import dev.amigoes.starlighttimer.core.identity.domain.Perfil;

import java.util.Objects;

public class PerfilMapper {
    public static PerfilEntity paraEntity(Perfil perfil) {
        Objects.requireNonNull(perfil, "O perfil e obrigatorio");
        return new PerfilEntity(perfil.getUsuarioId(), perfil.getNomeExibido(),
                perfil.getAvatar(), perfil.getBio(), perfil.getFusoHorario(), perfil.getXpTotal());
    }

    public static Perfil paraDominio(PerfilEntity entity) {
        Objects.requireNonNull(entity, "O perfil persistido e obrigatorio");
        if (entity.getXpTotal() == null) {
            throw new IllegalStateException("Perfil persistido sem xp_total");
        }
        return new Perfil(entity.getUsuarioId(), entity.getNomeExibido(),
                entity.getAvatar(), entity.getBio(), entity.getFusoHorario(), entity.getXpTotal());
    }
}
