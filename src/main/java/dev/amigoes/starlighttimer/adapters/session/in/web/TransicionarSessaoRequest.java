package dev.amigoes.starlighttimer.adapters.session.in.web;

import dev.amigoes.starlighttimer.domain.session.enums.StatusSessao;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;

public record TransicionarSessaoRequest(@NotNull StatusSessao status, OffsetDateTime reportadoEm) {}
