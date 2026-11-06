package dev.amigoes.starlighttimer.domain.progression.entities;

import java.util.Set;
import java.util.UUID;

public record Constelacao(UUID id, String codigo, String nome, int xpRecompensa,
                         int qtdEstrelas, int qtdInsigniasRequeridas, String descricao,
                         String assetSvg, Set<UUID> insignias) {
    public Constelacao {
        if (id == null || codigo == null || nome == null || insignias == null) {
            throw new IllegalArgumentException("Dados obrigatorios da constelacao ausentes");
        }
        if (xpRecompensa < 0 || qtdEstrelas <= 0 || qtdInsigniasRequeridas <= 0
                || qtdInsigniasRequeridas > insignias.size()) {
            throw new IllegalArgumentException("Catalogo de constelacao invalido");
        }
        insignias = Set.copyOf(insignias);
    }
}
