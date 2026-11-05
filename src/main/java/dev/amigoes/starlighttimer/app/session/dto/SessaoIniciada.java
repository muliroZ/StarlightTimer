package dev.amigoes.starlighttimer.app.session.dto;

import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.entities.SessaoEvento;

public record SessaoIniciada(Sessao sessao, SessaoEvento evento) {
}
