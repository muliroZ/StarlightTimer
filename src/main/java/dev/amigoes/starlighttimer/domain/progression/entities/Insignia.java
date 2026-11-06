package dev.amigoes.starlighttimer.domain.progression.entities;

import dev.amigoes.starlighttimer.domain.progression.enums.TipoCriterio;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RegraDeNegocioException;

import java.util.Objects;
import java.util.UUID;

public record Insignia(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        String icone,
        Integer xpRecompensa,
        String criterioJson,
        TipoCriterio tipoCriterio,
        Integer contagemAlvo
) {
    public Insignia {
        Objects.requireNonNull(id, "O id e obrigatorio");
        Objects.requireNonNull(codigo, "O codigo e obrigatorio");
        Objects.requireNonNull(nome, "O nome e obrigatorio");
        Objects.requireNonNull(xpRecompensa, "A recompensa e obrigatoria");
        Objects.requireNonNull(criterioJson, "O json de criterio e obrigatorio");
        Objects.requireNonNull(contagemAlvo, "A contagem total e obrigatoria");

        if (xpRecompensa <= 0) {
            throw new RegraDeNegocioException("A recompensa deve ser positiva");
        }

        if (contagemAlvo <= 0) {
            throw new RegraDeNegocioException("A meta final deve ser informada");
        }
    }
}
