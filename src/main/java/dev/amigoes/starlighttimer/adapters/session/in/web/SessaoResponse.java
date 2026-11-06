package dev.amigoes.starlighttimer.adapters.session.in.web;

import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.enums.StatusSessao;
import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;
import dev.amigoes.starlighttimer.domain.session.events.SessaoConcluida;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SessaoResponse(UUID id, TipoSessao tipo, StatusSessao status, int duracaoPlanejada,
                             int tempoDeFoco, int tempoDePausa, LocalDate diaLocal,
                             OffsetDateTime iniciadoEm, OffsetDateTime terminadoEm,
                             OffsetDateTime pausaIniciadaEm, OffsetDateTime horarioServidor,
                             SessaoConcluida.Recompensa recompensa) {
    public static SessaoResponse deDominio(Sessao sessao, OffsetDateTime horarioServidor,
                                            SessaoConcluida.Recompensa recompensa) {
        return new SessaoResponse(sessao.getId(), sessao.getTipo(), sessao.getStatus(),
                sessao.getDuracaoPlanejada(), sessao.getTempoDeFoco(), sessao.getTempoDePausa(),
                sessao.getDiaLocalAsLocalDate(), sessao.getIniciadoEm(), sessao.getTerminadoEm(),
                sessao.getPausaIniciadaEm(), horarioServidor, recompensa);
    }
}
