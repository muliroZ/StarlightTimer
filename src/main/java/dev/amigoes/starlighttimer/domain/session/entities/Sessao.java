package dev.amigoes.starlighttimer.domain.session.entities;

import dev.amigoes.starlighttimer.domain.session.enums.*;
import dev.amigoes.starlighttimer.domain.session.exceptions.ConclusaoAntecipadaException;
import dev.amigoes.starlighttimer.domain.session.exceptions.TransicaoInvalidaException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId e obrigatorio");
        this.tipo = Objects.requireNonNull(tipo, "tipo e obrigatorio");
        this.duracaoPlanejada = Objects.requireNonNull(duracaoPlanejada, "duracaoPlanejada e obrigatoria");
        if (duracaoPlanejada <= 0) {
            throw new IllegalArgumentException("duracaoPlanejada deve ser positiva");
        }
        this.diaLocal = new Date(Objects.requireNonNull(diaLocal, "diaLocal e obrigatorio").getTime());
        this.iniciadoEm = Objects.requireNonNull(iniciadoEm, "iniciadoEm e obrigatorio");
        this.status = StatusSessao.RUNNING;
        this.tempoDeFoco = 0;
        this.tempoDePausa = 0;
    }

    public Sessao(UUID id, UUID usuarioId, TipoSessao tipo, StatusSessao status,
                  int duracaoPlanejada, int tempoDeFoco, int tempoDePausa,
                  Date diaLocal, OffsetDateTime iniciadoEm, OffsetDateTime terminadoEm,
                  OffsetDateTime pausaIniciadaEm) {
        this.id = Objects.requireNonNull(id, "id e obrigatorio");
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId e obrigatorio");
        this.tipo = Objects.requireNonNull(tipo, "tipo e obrigatorio");
        this.status = Objects.requireNonNull(status, "status e obrigatorio");
        this.duracaoPlanejada = duracaoPlanejada;
        this.tempoDeFoco = tempoDeFoco;
        this.tempoDePausa = tempoDePausa;
        this.diaLocal = diaLocal != null ? new Date(diaLocal.getTime()) : null;
        this.iniciadoEm = Objects.requireNonNull(iniciadoEm, "iniciadoEm e obrigatorio");
        this.terminadoEm = terminadoEm;
        this.pausaIniciadaEm = pausaIniciadaEm;
    }

    public SessaoEvento eventoInicial(OffsetDateTime reportadoEm) {
        return criarEvento(TipoEvento.INICIADA, "{}", iniciadoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento pausar(OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        if (tipo != TipoSessao.FOCUS) {
            throw new TransicaoInvalidaException("Apenas sessoes de foco podem ser pausadas");
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
            return abandonar("Tempo maximo de pausa excedido (10 min)", ocorridoEm, reportadoEm);
        }
        status = proximo;
        tempoDePausa = pausaTotal;
        pausaIniciadaEm = null;
        return criarEvento(TipoEvento.RETOMADA, "{\"tempoDePausaSegundos\":%d}".formatted(tempoDePausa), ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento concluir(OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        return concluir(ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento concluir(OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm, OrigemEvento origem) {
        StatusSessao proximo = status.changeStatus(StatusSessao.COMPLETED);
        if (status != StatusSessao.RUNNING) {
            throw new TransicaoInvalidaException("A conclusao exige uma sessao em andamento");
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
        return criarEvento(TipoEvento.CONCLUIDA, "{\"tempoDeFocoSegundos\":%d}".formatted(tempoDeFoco), ocorridoEm, reportadoEm, origem);
    }

    public SessaoEvento conclusaoRejeitada(int segundosRestantes, OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        return criarEvento(TipoEvento.CONCLUSAO_REJEITADA,
                "{\"segundosRestantes\":%d}".formatted(segundosRestantes), ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento abandonar(String motivo, OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm) {
        return abandonar(motivo, ocorridoEm, reportadoEm, OrigemEvento.CLIENTE);
    }

    public SessaoEvento abandonar(String motivo, OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm, OrigemEvento origem) {
        StatusSessao proximo = status.changeStatus(StatusSessao.ABANDONED);
        Objects.requireNonNull(motivo, "motivo e obrigatorio");
        Objects.requireNonNull(origem, "origem e obrigatoria");
        int pausaTotal = status == StatusSessao.PAUSED ? pausaTotalAte(ocorridoEm) : tempoDePausa;
        if (status == StatusSessao.RUNNING) {
            tempoDeFoco = focoAte(ocorridoEm, tempoDePausa);
        }
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
            throw new IllegalArgumentException("O horario do evento nao pode anteceder o inicio do intervalo");
        }
        return segundos;
    }

    private SessaoEvento criarEvento(TipoEvento tipoEvento, String payload, OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm, OrigemEvento origem) {
        return new SessaoEvento(UUID.randomUUID(), id, payload, tipoEvento, ocorridoEm, reportadoEm, origem);
    }


    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public TipoSessao getTipo() {
        return tipo;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public int getDuracaoPlanejada() {
        return duracaoPlanejada;
    }

    public int getTempoDeFoco() {
        return tempoDeFoco;
    }

    public int getTempoDePausa() {
        return tempoDePausa;
    }

    public Date getDiaLocal() {
        return diaLocal != null ? new Date(diaLocal.getTime()) : null;
    }

    public LocalDate getDiaLocalAsLocalDate() {
        return diaLocal != null ? diaLocal.toInstant().atZone(ZoneOffset.UTC).toLocalDate() : null;
    }

    public OffsetDateTime getIniciadoEm() {
        return iniciadoEm;
    }

    public OffsetDateTime getTerminadoEm() {
        return terminadoEm;
    }

    public OffsetDateTime getPausaIniciadaEm() {
        return pausaIniciadaEm;
    }
}
