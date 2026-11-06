package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.domain.social.entities.Bloqueio;
import org.springframework.stereotype.Component;

@Component
public class BloqueioMapper {
    public Bloqueio paraDominio(ListaBloqueioEntity entity) {
        var id = entity.getId();
        return new Bloqueio(id.getUsuarioId(), id.getBloqueadoId());
    }
}
