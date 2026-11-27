package dev.amigoes.starlighttimer.records.session;

import dev.amigoes.starlighttimer.core.session.domain.Sessao;
import dev.amigoes.starlighttimer.events.SessaoConcluida;

public record ResultadoTransicao(Sessao sessao, SessaoConcluida.Recompensa recompensa) {}
