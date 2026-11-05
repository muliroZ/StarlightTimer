package dev.amigoes.starlighttimer.domain.progression.entities;

import java.util.Objects;
import java.util.UUID;

public record Patente(
    UUID id,
    String codigo,
    String nome,
    int nivelMinimo,
    String imagemTitulo
) {
    public Patente {
        Objects.requireNonNull(id, "O id e obrigatorio");
        Objects.requireNonNull(codigo, "O codigo e obrigatorio");
        Objects.requireNonNull(nome, "O nome e obrigatorio");

        if (nivelMinimo < 1 || nivelMinimo > 60) {
            throw new IllegalArgumentException(
                "O nivel minimo deve estar entre 1 e 60"
            );
        }
    }
}
