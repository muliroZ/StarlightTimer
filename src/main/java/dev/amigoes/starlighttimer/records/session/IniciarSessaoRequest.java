package dev.amigoes.starlighttimer.records.session;

import dev.amigoes.starlighttimer.enums.session.TipoSessao;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record IniciarSessaoRequest(@NotNull TipoSessao tipo, Integer duracaoPlanejada, OffsetDateTime reportadoEm) {}
