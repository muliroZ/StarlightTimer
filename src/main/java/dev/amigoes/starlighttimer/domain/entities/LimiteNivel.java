package dev.amigoes.starlighttimer.domain.entities;

public record LimiteNivel(int nivel, int xpNecessario) {

    public LimiteNivel {
        if (nivel < 1 || nivel > 60) {
            throw new IllegalArgumentException(
                "O nivel deve estar entre 1 e 60"
            );
        }

        if (xpNecessario < 0) {
            throw new IllegalArgumentException(
                "O XP necessario nao pode ser negativo"
            );
        }
    }
}