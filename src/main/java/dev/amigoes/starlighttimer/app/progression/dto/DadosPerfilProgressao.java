package dev.amigoes.starlighttimer.app.progression.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public record DadosPerfilProgressao(int xpTotal, ZoneId fuso, List<InsigniaUsuario> insignias,
                                    List<ConstelacaoUsuario> constelacoes) {
    public DadosPerfilProgressao {
        insignias = List.copyOf(insignias);
        constelacoes = List.copyOf(constelacoes);
    }

    public record InsigniaUsuario(UUID id, int contagem, Instant desbloqueadoEm) { }

    public record ConstelacaoUsuario(UUID id, Instant desbloqueadoEm) { }
}
