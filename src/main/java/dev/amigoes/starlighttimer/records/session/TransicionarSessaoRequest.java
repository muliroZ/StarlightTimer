package dev.amigoes.starlighttimer.records.session;

import dev.amigoes.starlighttimer.enums.session.StatusSessao;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record TransicionarSessaoRequest(@NotNull StatusSessao status, OffsetDateTime reportadoEm) {}
