package dev.amigoes.starlighttimer.domain.session.events;

import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SessaoConcluida(
        UUID sessaoId,
        UUID usuarioId,
        TipoSessao tipo,
        int duracaoPlanejada,
        Instant terminadoEm
) {
    public SessaoConcluida {
        Objects.requireNonNull(sessaoId, "sessaoId é obrigatório");
        Objects.requireNonNull(usuarioId, "usuarioId é obrigatório");
        Objects.requireNonNull(tipo, "tipo é obrigatório");
        Objects.requireNonNull(terminadoEm, "terminadoEm é obrigatório");
        if (duracaoPlanejada <= 0) {
            throw new IllegalArgumentException("duracaoPlanejada deve ser positiva");
        }
    }

    /** Permite ao consumidor do evento reconhecer foco sem importar o modelo da sessao. */
    public boolean ehFoco() {
        return tipo == TipoSessao.FOCUS;
    }
}
