package dev.amigoes.starlighttimer.domain.progression.entities;

import java.util.Objects;
import java.util.OptionalInt;

public record ResultadoProgressao(
    int nivel,
    Patente patente,
    OptionalInt xpDoProximoNivel
) {
    public ResultadoProgressao {
        Objects.requireNonNull(patente, "A patente e obrigatoria");
        Objects.requireNonNull(
            xpDoProximoNivel,
            "O proximo limite deve ser informado ou vazio"
        );
    }
}
