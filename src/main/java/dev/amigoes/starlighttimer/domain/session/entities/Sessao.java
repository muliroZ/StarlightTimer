package dev.amigoes.starlighttimer.domain.session.entities;

import dev.amigoes.starlighttimer.domain.session.enums.*;
import dev.amigoes.starlighttimer.domain.session.exceptions.ConclusaoAntecipadaException;
import dev.amigoes.starlighttimer.domain.session.exceptions.TransicaoInvalidaException;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Sessao {
    private static final int LIMITE_PAUSA_SEGUNDOS = 600;
    private static final int TOLERANCIA_SEGUNDOS = 5;

    private final UUID id;
    private final UUID usuarioId;
    private final TipoSessao tipo;
    private StatusSessao status;
    private final int duracaoPlanejada;
    private int tempoDeFoco;
    private int tempoDePausa;
    private final Date diaLocal;
    private final OffsetDateTime iniciadoEm;
    private OffsetDateTime terminadoEm;
    private OffsetDateTime pausaIniciadaEm;

    public Sessao(UUID usuarioId, Integer duracaoPlanejada, Date diaLocal, OffsetDateTime iniciadoEm) {
        this(usuarioId, TipoSessao.FOCUS, duracaoPlanejada, diaLocal, iniciadoEm);
    }

    public Sessao(UUID usuarioId, TipoSessao tipo, Integer duracaoPlanejada, Date diaLocal, OffsetDateTime iniciadoEm) {
        this.id = UUID.randomUUID();
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId é obrigatório");
        this.tipo = Objects.requireNonNull(tipo, "tipo é obrigatório");
        this.duracaoPlanejada = Objects.requireNonNull(duracaoPlanejada, "duracaoPlanejada é obrigatória");
        if (duracaoPlanejada <= 0) {
            throw new IllegalArgumentException("duracaoPlanejada deve ser positiva");
        }
        this.diaLocal = new Date(Objects.requireNonNull(diaLocal, "diaLocal é obrigatório").getTime());
        this.iniciadoEm = Objects.requireNonNull(iniciadoEm, "iniciadoEm é obrigatório");
        this.status = StatusSessao.RUNNING;
    }

    public SessaoEvento eventoInicial(OffsetDateTime reportadoEm) {
        return criarEvento(TipoEvento.INICIADA, "{}", iniciadoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento pausar(OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        if (tipo != TipoSessao.FOCUS) {
            throw new TransicaoInvalidaException("Apenas sessões de foco podem ser pausadas");
        }
        StatusSessao proximo = status.changeStatus(StatusSessao.PAUSED);
        int focoCalculado = focoAte(ocorridoEm, tempoDePausa);
        status = proximo;
        tempoDeFoco = focoCalculado;
        pausaIniciadaEm = ocorridoEm;
        return criarEvento(TipoEvento.PAUSADA, "{\"tempoDeFocoSegundos\":%d}".formatted(tempoDeFoco), ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento retomar(OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        StatusSessao proximo = status.changeStatus(StatusSessao.RUNNING);
        int pausaTotal = pausaTotalAte(ocorridoEm);
        if (pausaTotal > LIMITE_PAUSA_SEGUNDOS) {
            return abandonar("Tempo máximo de pausa excedido (10 min)", ocorridoEm, reportadoEm);
        }
        status = proximo;
        tempoDePausa = pausaTotal;
        pausaIniciadaEm = null;
        return criarEvento(TipoEvento.RETOMADA, "{\"tempoDePausaSegundos\":%d}".formatted(tempoDePausa), ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento concluir(OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        StatusSessao proximo = status.changeStatus(StatusSessao.COMPLETED);
        if (status != StatusSessao.RUNNING) {
            throw new TransicaoInvalidaException("A conclusão exige uma sessão em andamento");
        }
        int focoCalculado = focoAte(ocorridoEm, tempoDePausa);
        int restantes = duracaoPlanejada - focoCalculado;
        if (restantes > TOLERANCIA_SEGUNDOS) {
            throw new ConclusaoAntecipadaException(restantes);
        }
        OffsetDateTime fimPlanejado = iniciadoEm.plusSeconds((long) duracaoPlanejada + tempoDePausa);
        OffsetDateTime fim = restantes > 0 ? ocorridoEm : fimPlanejado;
        int focoFinal = focoAte(fim, tempoDePausa);
        status = proximo;
        tempoDeFoco = focoFinal;
        terminadoEm = fim;
        return criarEvento(TipoEvento.CONCLUIDA, "{\"tempoDeFocoSegundos\":%d}".formatted(tempoDeFoco), ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento abandonar(String motivo, OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        return abandonar(motivo, ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento abandonar(String motivo, OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm, OrigemEvento origem) {
        StatusSessao proximo = status.changeStatus(StatusSessao.ABANDONED);
        Objects.requireNonNull(motivo, "motivo é obrigatório");
        Objects.requireNonNull(origem, "origem é obrigatória");
        int pausaTotal = status == StatusSessao.PAUSED ? pausaTotalAte(ocorridoEm) : tempoDePausa;
        status = proximo;
        tempoDePausa = pausaTotal;
        pausaIniciadaEm = null;
        terminadoEm = ocorridoEm;
        String motivoJson = motivo.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
        return criarEvento(TipoEvento.ABANDONADA, "{\"motivo\":\"%s\"}".formatted(motivoJson), ocorridoEm, reportadoEm, origem);
    }

    private int pausaTotalAte(OffsetDateTime ocorridoEm) {
        return Math.addExact(tempoDePausa, Math.toIntExact(segundosEntre(pausaIniciadaEm, ocorridoEm)));
    }

    private int focoAte(OffsetDateTime ocorridoEm, int pausaSegundos) {
        return Math.toIntExact(segundosEntre(iniciadoEm, ocorridoEm) - pausaSegundos);
    }

    private long segundosEntre(OffsetDateTime inicio, OffsetDateTime fim) {
        long segundos = Duration.between(Objects.requireNonNull(inicio), Objects.requireNonNull(fim)).getSeconds();
        if (segundos < 0) {
            throw new IllegalArgumentException("O horário do evento não pode anteceder o início do intervalo");
        }
        return segundos;
    }

    private SessaoEvento criarEvento(TipoEvento tipoEvento, String payload, OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm, OrigemEvento origem) {
        return new SessaoEvento(UUID.randomUUID(), id, payload, tipoEvento, ocorridoEm, reportadoEm, origem);
    }
}
