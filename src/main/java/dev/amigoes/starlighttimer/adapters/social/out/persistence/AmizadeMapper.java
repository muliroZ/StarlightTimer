package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.domain.social.entities.Amizade;
import org.springframework.stereotype.Component;

@Component
public class AmizadeMapper {
    public Amizade paraDominio(AmizadeEntity entity) {
        return new Amizade(entity.getId(), entity.getUsuario1Id(),
                entity.getUsuario2Id(), entity.getEstabelecidaEm());
    }
}
