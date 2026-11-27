package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.records.social.Amizade;

public class AmizadeMapper {
    public static Amizade paraDominio(AmizadeEntity entity) {
        return new Amizade(entity.getId(), entity.getUsuario1Id(),
                entity.getUsuario2Id(), entity.getEstabelecidaEm());
    }
}
