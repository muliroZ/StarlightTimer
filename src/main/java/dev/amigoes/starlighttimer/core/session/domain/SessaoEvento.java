package dev.amigoes.starlighttimer.core.session.domain;

import dev.amigoes.starlighttimer.enums.session.OrigemEvento;
import dev.amigoes.starlighttimer.enums.session.TipoEvento;

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
