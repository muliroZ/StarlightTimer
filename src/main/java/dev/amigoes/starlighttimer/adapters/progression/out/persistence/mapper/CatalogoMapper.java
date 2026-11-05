package dev.amigoes.starlighttimer.adapters.progression.out.persistence.mapper;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.LimiteNivelEntity;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.PatenteEntity;
import dev.amigoes.starlighttimer.domain.progression.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.progression.entities.Patente;
import org.springframework.stereotype.Component;

@Component
public class CatalogoMapper {

    public LimiteNivel toDomain(LimiteNivelEntity entity) {
        return new LimiteNivel(entity.getNivel(), entity.getXpNecessario());
    }

    public Patente toDomain(PatenteEntity entity) {
        return new Patente(
                entity.getId(),
                entity.getCodigo(),
                entity.getNome(),
                entity.getNivelMinimo().getNivel(),
                entity.getImagemTitulo()
        );
    }
}
