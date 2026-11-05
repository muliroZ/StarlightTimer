package dev.amigoes.starlighttimer.domain.session.entities;

import dev.amigoes.starlighttimer.domain.session.enums.OrigemEvento;
import dev.amigoes.starlighttimer.domain.session.enums.TipoEvento;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessaoEvento(
        UUID id,
        UUID sessaoId,
        String payload,
        TipoEvento tipoEvento,
        OffsetDateTime ocorridoEm,
        OffsetDateTime reportadoEm,
        OrigemEvento origem
) {
}
