package dev.amigoes.starlighttimer.domain.services;

import dev.amigoes.starlighttimer.domain.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.entities.Patente;
import dev.amigoes.starlighttimer.domain.entities.ResultadoProgressao;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public final class CurvaDeNiveis {

    private static final int NIVEL_MAXIMO = 60;

    private final List<LimiteNivel> limites;
    private final List<Patente> patentes;

    public CurvaDeNiveis(
        List<LimiteNivel> limites,
        List<Patente> patentes
    ) {
        Objects.requireNonNull(limites, "Os limites sao obrigatorios");
        Objects.requireNonNull(patentes, "As patentes sao obrigatorias");

        this.limites = limites.stream()
            .sorted(Comparator.comparingInt(LimiteNivel::nivel))
            .toList();

        this.patentes = patentes.stream()
            .sorted(Comparator.comparingInt(Patente::nivelMinimo))
            .toList();

        validarCatalogos();
    }

    private void validarCatalogos() {
        if (limites.size() != NIVEL_MAXIMO) {
            throw new IllegalArgumentException(
                "O catalogo deve conter os niveis de 1 a 60"
            );
        }

        if (limites.getFirst().xpNecessario() != 0) {
            throw new IllegalArgumentException(
                "O nivel 1 deve exigir zero XP"
            );
        }

        for (int i = 0; i < limites.size(); i++) {
            LimiteNivel atual = limites.get(i);

            if (atual.nivel() != i + 1) {
                throw new IllegalArgumentException(
                    "Os niveis devem ser consecutivos e sem repeticoes"
                );
            }

            if (i > 0
                && atual.xpNecessario()
                <= limites.get(i - 1).xpNecessario()) {
                throw new IllegalArgumentException(
                    "Os limites de XP devem ser crescentes"
                );
            }
        }

        if (patentes.isEmpty()
            || patentes.getFirst().nivelMinimo() != 1) {
            throw new IllegalArgumentException(
                "Deve existir uma patente inicial no nivel 1"
            );
        }

        for (int i = 1; i < patentes.size(); i++) {
            if (patentes.get(i).nivelMinimo()
                == patentes.get(i - 1).nivelMinimo()) {
                throw new IllegalArgumentException(
                    "As patentes devem ter niveis minimos distintos"
                );
            }
        }
    }

    public ResultadoProgressao calcular(int xpTotal) {
        if (xpTotal < 0) {
            throw new IllegalArgumentException(
                "O XP total nao pode ser negativo"
            );
        }

        int nivel = 1;

        for (LimiteNivel limite : limites) {
            if (xpTotal < limite.xpNecessario()) {
                break;
            }

            nivel = limite.nivel();
        }

        Patente patenteAtual = patentes.getFirst();

        for (Patente patente : patentes) {
            if (patente.nivelMinimo() > nivel) {
                break;
            }

            patenteAtual = patente;
        }

        OptionalInt proximoLimite = nivel == NIVEL_MAXIMO
            ? OptionalInt.empty()
            : OptionalInt.of(limites.get(nivel).xpNecessario());

        return new ResultadoProgressao(
            nivel,
            patenteAtual,
            proximoLimite
        );
    }
}