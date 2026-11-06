package dev.amigoes.starlighttimer.domain.progression.services;

import dev.amigoes.starlighttimer.domain.session.events.SessaoConcluida;

import java.util.Objects;

public final class CalculadoraXp {

    private static final int SEGUNDOS_POR_MINUTO = 60;

    public int calcular(SessaoConcluida evento) {
        Objects.requireNonNull(evento, "O evento e obrigatorio");

        if (!evento.ehFoco()) {
            return 0;
        }

        int duracaoEmSegundos = evento.duracaoPlanejada();

        if (duracaoEmSegundos % SEGUNDOS_POR_MINUTO != 0) {
            throw new IllegalArgumentException(
                "A duracao planejada deve representar minutos inteiros"
            );
        }

        return duracaoEmSegundos / SEGUNDOS_POR_MINUTO;
    }

    public int calcularRecompensaInsignia(int xpRecompensa) {
        return validarRecompensa(xpRecompensa);
    }

    public int calcularRecompensaConstelacao(int xpRecompensa) {
        return validarRecompensa(xpRecompensa);
    }

    private int validarRecompensa(int xpRecompensa) {
        if (xpRecompensa < 0) {
            throw new IllegalArgumentException(
                "A recompensa de XP nao pode ser negativa"
            );
        }

        return xpRecompensa;
    }
}
