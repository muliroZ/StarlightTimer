package dev.amigoes.starlighttimer.app.progression.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public record HistoricoProgressao(ZoneId fuso, List<Ciclo> ciclos, List<Nota> notas) {
    public HistoricoProgressao {
        ciclos = List.copyOf(ciclos);
        notas = List.copyOf(notas);
    }

    public record Ciclo(UUID id, boolean foco, boolean descanso, int segundosFoco,
                        int segundosPausa, LocalDate diaLocal, Instant iniciadoEm,
                        Instant terminadoEm) { }

    public record Nota(UUID sessaoId, Instant criadoEm) { }
}
