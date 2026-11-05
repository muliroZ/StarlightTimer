package dev.amigoes.starlighttimer.domain.entities;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ProgressoInsignia {

    private final UUID usuarioId;
    private final UUID insigniaId;

    private int contagemProgresso;
    private Instant desbloqueadoEm;

    public ProgressoInsignia(
        UUID usuarioId,
        UUID insigniaId,
        int contagemProgresso,
        Instant desbloqueadoEm
    ) {
        this.usuarioId = Objects.requireNonNull(
            usuarioId, "O usuario e obrigatorio"
        );

        this.insigniaId = Objects.requireNonNull(
            insigniaId, "A insignia e obrigatoria"
        );

        if (contagemProgresso < 0) {
            throw new IllegalArgumentException(
                "O progresso nao pode ser negativo"
            );
        }

        this.contagemProgresso = contagemProgresso;
        this.desbloqueadoEm = desbloqueadoEm;
    }

    public boolean incrementar(int contagemAlvo, Clock clock) {
        if (contagemAlvo <= 0) {
            throw new IllegalArgumentException(
                "A contagem alvo deve ser positiva"
            );
        }

        Objects.requireNonNull(clock, "O Clock e obrigatorio");

        int novaContagem = Math.addExact(contagemProgresso, 1);

        boolean desbloqueouAgora =
            desbloqueadoEm == null && novaContagem >= contagemAlvo;

        Instant instanteDesbloqueio = desbloqueouAgora
            ? clock.instant()
            : desbloqueadoEm;

        contagemProgresso = novaContagem;
        desbloqueadoEm = instanteDesbloqueio;

        return desbloqueouAgora;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getInsigniaId() {
        return insigniaId;
    }

    public int getContagemProgresso() {
        return contagemProgresso;
    }

    public Instant getDesbloqueadoEm() {
        return desbloqueadoEm;
    }

    public boolean estaDesbloqueada() {
        return desbloqueadoEm != null;
    }
}