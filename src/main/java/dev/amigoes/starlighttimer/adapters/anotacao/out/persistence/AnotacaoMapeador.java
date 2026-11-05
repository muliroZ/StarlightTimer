package dev.amigoes.starlighttimer.adapters.anotacao.out.persistence;

import dev.amigoes.starlighttimer.domain.anotacao.Anotacao;

public class AnotacaoMapeador {

    public static AnotacaoEntidade paraEntidade(Anotacao dominio) {
        if (dominio == null) return null;
        return new AnotacaoEntidade(
                dominio.getId(),
                dominio.getUsuarioId(),
                dominio.getSessaoId(),
                dominio.getConteudo(),
                dominio.getDataAnotacao(),
                dominio.getCriadoEm(),
                dominio.getAtualizadoEm()
        );
    }

    public static Anotacao paraDominio(AnotacaoEntidade entidade) {
        if (entidade == null) return null;
        return new Anotacao(
                entidade.getId(),
                entidade.getUsuarioId(),
                entidade.getSessaoId(),
                entidade.getConteudo(),
                entidade.getDataAnotacao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}