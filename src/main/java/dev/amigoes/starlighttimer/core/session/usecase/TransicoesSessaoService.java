package dev.amigoes.starlighttimer.core.session.usecase;

import dev.amigoes.starlighttimer.core.session.domain.Sessao;
import dev.amigoes.starlighttimer.core.session.domain.SessaoEvento;
import dev.amigoes.starlighttimer.enums.session.OrigemEvento;
import dev.amigoes.starlighttimer.enums.session.TipoSessao;
import dev.amigoes.starlighttimer.records.session.SessaoIniciada;

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

    public SessaoEvento concluirPorAgendador(Sessao sessao) {
        return sessao.concluir(agora(), null, OrigemEvento.AGENDADOR);
    }

    public OffsetDateTime horarioServidor() {
        return agora();
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
