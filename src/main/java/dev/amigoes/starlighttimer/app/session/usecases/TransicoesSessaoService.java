package dev.amigoes.starlighttimer.app.session.usecases;

import dev.amigoes.starlighttimer.app.session.dto.SessaoIniciada;
import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.entities.SessaoEvento;
import dev.amigoes.starlighttimer.domain.session.enums.OrigemEvento;
import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransicoesSessaoService {
    private final Clock relogio;

    public TransicoesSessaoService(Clock relogio) {
        this.relogio = Objects.requireNonNull(relogio);
    }

    public SessaoIniciada iniciar(UUID usuarioId, TipoSessao tipo, int duracaoPlanejada, ZoneId fusoUsuario, OffsetDateTime reportadoEm) {
        OffsetDateTime ocorridoEm = agora();
        LocalDate diaLocal = ocorridoEm.atZoneSameInstant(Objects.requireNonNull(fusoUsuario)).minusHours(4).toLocalDate();
        Sessao sessao = new Sessao(usuarioId, tipo, duracaoPlanejada, Date.from(diaLocal.atStartOfDay(ZoneOffset.UTC).toInstant()), ocorridoEm);
        return new SessaoIniciada(sessao, sessao.eventoInicial(reportadoEm));
    }

    public SessaoEvento pausar(Sessao sessao, OffsetDateTime reportadoEm) {
        return sessao.pausar(agora(), reportadoEm);
    }

    public SessaoEvento retomar(Sessao sessao, OffsetDateTime reportadoEm) {
        return sessao.retomar(agora(), reportadoEm);
    }

    public SessaoEvento concluir(Sessao sessao, OffsetDateTime reportadoEm) {
        return sessao.concluir(agora(), reportadoEm);
    }

    public SessaoEvento abandonar(Sessao sessao, String motivo, OffsetDateTime reportadoEm) {
        return sessao.abandonar(motivo, agora(), reportadoEm);
    }

    public SessaoEvento abandonarPorAgendador(Sessao sessao, String motivo) {
        return sessao.abandonar(motivo, agora(), null, OrigemEvento.AGENDADOR);
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.ofInstant(relogio.instant(), ZoneOffset.UTC);
    }
}
