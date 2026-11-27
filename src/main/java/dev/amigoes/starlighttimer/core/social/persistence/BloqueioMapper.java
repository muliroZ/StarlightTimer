package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.records.social.Bloqueio;

public class BloqueioMapper {
    public static Bloqueio paraDominio(ListaBloqueioEntity entity) {
        var id = entity.getId();
        return new Bloqueio(id.getUsuarioId(), id.getBloqueadoId());
    }
}
