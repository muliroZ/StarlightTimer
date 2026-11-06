package dev.amigoes.starlighttimer.adapters.progression.in.web;

import dev.amigoes.starlighttimer.app.progression.dto.ResumoProgressao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProgressaoResponse(int nivel, Patente patente, int xpTotal, int xpAtual,
                                 Integer xpDoProximoNivel, Integer xpParaProximoNivel,
                                 long estrelasForjadas, long segundosEmOrbit,
                                 BigDecimal horasEmOrbit, int orbitAtual,
                                 List<Insignia> insignias, List<Constelacao> constelacoes) {
    public static ProgressaoResponse de(ResumoProgressao resumo) {
        var patente = new Patente(resumo.patente().codigo(), resumo.patente().nome(),
                resumo.patente().imagemTitulo());
        var insignias = resumo.insignias().stream().map(item -> new Insignia(
                item.codigo(), item.nome(), item.descricao(), item.icone(), item.progresso(),
                item.alvo(), item.desbloqueada(), item.desbloqueadoEm())).toList();
        var constelacoes = resumo.constelacoes().stream().map(item -> new Constelacao(
                item.codigo(), item.nome(), item.descricao(), item.assetSvg(), item.estrelas(),
                item.estrelasAlvo(), item.insignias(), item.insigniasAlvo(), item.desbloqueada(),
                item.desbloqueadoEm())).toList();
        return new ProgressaoResponse(resumo.nivel(), patente, resumo.xpTotal(), resumo.xpAtual(),
                resumo.xpDoProximoNivel(), resumo.xpParaProximoNivel(), resumo.estrelasForjadas(),
                resumo.segundosEmOrbit(), resumo.horasEmOrbit(), resumo.orbitAtual(),
                insignias, constelacoes);
    }

    public record Patente(String codigo, String nome, String imagemTitulo) { }

    public record Insignia(String codigo, String nome, String descricao, String icone,
                           int progresso, int alvo, boolean desbloqueada, Instant desbloqueadoEm) { }

    public record Constelacao(String codigo, String nome, String descricao, String assetSvg,
                              long estrelas, int estrelasAlvo, int insignias,
                              int insigniasAlvo, boolean desbloqueada, Instant desbloqueadoEm) { }
}
