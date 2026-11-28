package dev.amigoes.starlighttimer.config;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class RelogioAjustavel extends Clock {
    private final AtomicReference<Instant> agora;
    private final ZoneId zona;

    public RelogioAjustavel(Instant inicio) {
        this(new AtomicReference<>(Objects.requireNonNull(inicio)), ZoneOffset.UTC);
    }

    private RelogioAjustavel(AtomicReference<Instant> agora, ZoneId zona) {
        this.agora = agora;
        this.zona = Objects.requireNonNull(zona);
    }

    public void avancar(Duration duracao) {
        if (duracao.isNegative()) {
            throw new IllegalArgumentException("O relogio nao pode recuar");
        }
        agora.updateAndGet(instante -> instante.plus(duracao));
    }

    @Override
    public Instant instant() {
        return agora.get();
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId zona) {
        return new RelogioAjustavel(agora, zona);
    }
}
