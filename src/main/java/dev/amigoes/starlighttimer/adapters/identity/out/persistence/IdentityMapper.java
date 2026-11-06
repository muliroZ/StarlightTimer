package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.domain.social.entities.ConfiguracoesUsuario;
import dev.amigoes.starlighttimer.domain.social.entities.Perfil;

public class IdentityMapper {

    public static PerfilEntity paraEntidade(Perfil dominio) {
        if (dominio == null) return null;
        return new PerfilEntity(
                dominio.getUsuarioId(),
                dominio.getNomeExibido(),
                dominio.getAvatar(),
                dominio.getBio(),
                dominio.getFusoHorario(),
                dominio.getXpTotal()
        );
    }

    public static Perfil paraDominio(PerfilEntity entidade) {
        if (entidade == null) return null;
        return new Perfil(
                entidade.getUsuarioId(),
                entidade.getNomeExibido(),
                entidade.getAvatar(),
                entidade.getBio(),
                entidade.getFusoHorario(),
                entidade.getXpTotal() != null ? entidade.getXpTotal() : 0
        );
    }

    public static ConfiguracoesUsuarioEntity paraEntidade(ConfiguracoesUsuario dominio) {
        if (dominio == null) return null;
        return new ConfiguracoesUsuarioEntity(
                dominio.getUsuarioId(),
                dominio.getFocoEmSegundos(),
                dominio.getPausaEmSegundos(),
                dominio.isIniciarPausaAuto(),
                dominio.isSomHabilitado()
        );
    }

    public static ConfiguracoesUsuario paraDominio(ConfiguracoesUsuarioEntity entidade) {
        if (entidade == null) return null;
        return new ConfiguracoesUsuario(
                entidade.getUsuarioId(),
                entidade.getFocoEmSegundos() != null ? entidade.getFocoEmSegundos() : 1500,
                entidade.getPausaEmSegundos() != null ? entidade.getPausaEmSegundos() : 300,
                Boolean.TRUE.equals(entidade.getIniciarPausaAuto()),
                Boolean.TRUE.equals(entidade.getSomHabilitado())
        );
    }
}