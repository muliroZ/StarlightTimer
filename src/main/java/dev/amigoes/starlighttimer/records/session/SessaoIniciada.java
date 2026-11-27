package dev.amigoes.starlighttimer.records.session;

import dev.amigoes.starlighttimer.core.session.domain.Sessao;
import dev.amigoes.starlighttimer.core.session.domain.SessaoEvento;

public record SessaoIniciada(Sessao sessao, SessaoEvento evento) {
}
