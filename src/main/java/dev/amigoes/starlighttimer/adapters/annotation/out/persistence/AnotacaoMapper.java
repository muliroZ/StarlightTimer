package dev.amigoes.starlighttimer.adapters.annotation.out.persistence;

import dev.amigoes.starlighttimer.domain.annotation.Anotacao;

public class AnotacaoMapper {

    public static AnotacaoEntity paraEntidade(Anotacao dominio) {
        if (dominio == null) return null;
        return new AnotacaoEntity(
                dominio.getId(),
                dominio.getUsuarioId(),
                dominio.getSessaoId(),
                dominio.getConteudo(),
                dominio.getDataAnotacao(),
                dominio.getCriadoEm(),
                dominio.getAtualizadoEm()
        );
    }

    public static Anotacao paraDominio(AnotacaoEntity entidade) {
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