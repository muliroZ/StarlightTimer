package dev.amigoes.starlighttimer.domain.progression.entities;

import dev.amigoes.starlighttimer.domain.shared.exceptions.RegraDeNegocioException;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record GanhoXp(
        UUID id,
        UUID usuarioId,
        Integer quantidade,
        UUID origemSessao,
        UUID origemConstelacao,
        UUID origemInsignia,
        OffsetDateTime atribuidoEm
) {
    public GanhoXp {
        Objects.requireNonNull(id, "O id e obrigatorio");
        Objects.requireNonNull(usuarioId, "O id do usuario e obrigatorio");
        Objects.requireNonNull(quantidade, "A quantidade e obrigatoria");
        Objects.requireNonNull(atribuidoEm, "Data de atribuicao e obrigatoria");

        if (quantidade < 0) {
            throw new RegraDeNegocioException("A quantidade nao pode ser negativa");
        }

        int origensPreenchidas =
                (origemSessao != null ? 1 : 0)
                + (origemConstelacao != null ? 1 : 0)
                + (origemInsignia != null ? 1 : 0);

        if (origensPreenchidas != 1) {
            throw new IllegalArgumentException(
                    "O ganho de xp deve ter exatamente uma origem"
            );
        }
    }
}
