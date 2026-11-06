package dev.amigoes.starlighttimer.adapters.session.in.web;

import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;

public record IniciarSessaoRequest(@NotNull TipoSessao tipo, Integer duracaoPlanejada, OffsetDateTime reportadoEm) {}
