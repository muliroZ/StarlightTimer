package dev.amigoes.starlighttimer.domain.session.events;

import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Evento interno síncrono. O consumidor preenche a resposta antes de publishEvent retornar. */
public final class SessaoConcluida {
    private final UUID sessaoId;
    private final UUID usuarioId;
    private final TipoSessao tipo;
    private final int duracaoPlanejada;
    private final Instant terminadoEm;
    private Recompensa recompensa;

    public SessaoConcluida(UUID sessaoId, UUID usuarioId, TipoSessao tipo,
                           int duracaoPlanejada, Instant terminadoEm) {
        this.sessaoId = Objects.requireNonNull(sessaoId, "sessaoId e obrigatorio");
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId e obrigatorio");
        this.tipo = Objects.requireNonNull(tipo, "tipo e obrigatorio");
        this.terminadoEm = Objects.requireNonNull(terminadoEm, "terminadoEm e obrigatorio");
        if (duracaoPlanejada <= 0) throw new IllegalArgumentException("duracaoPlanejada deve ser positiva");
        this.duracaoPlanejada = duracaoPlanejada;
    }

    public UUID sessaoId() { return sessaoId; }
    public UUID usuarioId() { return usuarioId; }
    public TipoSessao tipo() { return tipo; }
    public int duracaoPlanejada() { return duracaoPlanejada; }
    public Instant terminadoEm() { return terminadoEm; }
    public boolean ehFoco() { return tipo == TipoSessao.FOCUS; }

    public void registrarRecompensa(Recompensa resultado) {
        if (recompensa != null) throw new IllegalStateException("Recompensa ja registrada");
        recompensa = Objects.requireNonNull(resultado);
    }

    public Recompensa recompensa() {
        if (recompensa == null) throw new IllegalStateException("Progressao ainda nao processada");
        return recompensa;
    }

    public record Recompensa(int xpGanho, List<String> novasInsignias,
                            List<String> novasConstelacoes, boolean subiuDeNivel,
                            int novoNivel) {
        public Recompensa {
            novasInsignias = List.copyOf(novasInsignias);
            novasConstelacoes = List.copyOf(novasConstelacoes);
        }
    }
}
