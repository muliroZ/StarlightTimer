package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.domain.identity.entities.Perfil;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class PerfilMapper {
    public PerfilEntity paraEntity(Perfil perfil) {
        Objects.requireNonNull(perfil, "O perfil e obrigatorio");
        return new PerfilEntity(perfil.getUsuarioId(), perfil.getNomeExibido(),
                perfil.getAvatar(), perfil.getBio(), perfil.getFusoHorario(), perfil.getXpTotal());
    }

    public Perfil paraDominio(PerfilEntity entity) {
        Objects.requireNonNull(entity, "O perfil persistido e obrigatorio");
        if (entity.getXpTotal() == null) {
            throw new IllegalStateException("Perfil persistido sem xp_total");
        }
        return new Perfil(entity.getUsuarioId(), entity.getNomeExibido(),
                entity.getAvatar(), entity.getBio(), entity.getFusoHorario(), entity.getXpTotal());
    }
}
