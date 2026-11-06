package dev.amigoes.starlighttimer.app.progression.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ResumoProgressao(int nivel, Patente patente, int xpTotal, int xpAtual,
                               Integer xpDoProximoNivel, Integer xpParaProximoNivel,
                               long estrelasForjadas, long segundosEmOrbit,
                               BigDecimal horasEmOrbit, int orbitAtual,
                               List<Insignia> insignias, List<Constelacao> constelacoes) {
    public ResumoProgressao {
        insignias = List.copyOf(insignias);
        constelacoes = List.copyOf(constelacoes);
    }

    public record Patente(String codigo, String nome, String imagemTitulo) { }

    public record Insignia(String codigo, String nome, String descricao, String icone,
                           int progresso, int alvo, boolean desbloqueada, Instant desbloqueadoEm) { }

    public record Constelacao(String codigo, String nome, String descricao, String assetSvg,
                              long estrelas, int estrelasAlvo, int insignias,
                              int insigniasAlvo, boolean desbloqueada, Instant desbloqueadoEm) { }
}
