package dev.amigoes.starlighttimer.core.identity.persistence;

import dev.amigoes.starlighttimer.core.identity.domain.Usuario;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UsuarioMapper {
    private final ConfiguracoesUsuarioMapper configuracoesMapper;

    public UsuarioMapper(ConfiguracoesUsuarioMapper configuracoesMapper) {
        this.configuracoesMapper = configuracoesMapper;
    }

    public UsuarioEntity paraEntity(Usuario usuario) {
        Objects.requireNonNull(usuario, "O usuario e obrigatorio");
        return new UsuarioEntity(usuario.getId(), usuario.getEmail(), usuario.getStatus(),
                usuario.getCriadoEm(), usuario.getExcluidoEm());
    }

    public Usuario paraDominio(UsuarioEntity usuario, PerfilEntity perfil,
                               ConfiguracoesUsuarioEntity configuracoes) {
        Objects.requireNonNull(usuario, "O usuario persistido e obrigatorio");
        Objects.requireNonNull(perfil, "O perfil persistido e obrigatorio");
        Objects.requireNonNull(configuracoes, "As configuracoes persistidas sao obrigatorias");
        if (!usuario.getId().equals(perfil.getUsuarioId())
                || !usuario.getId().equals(configuracoes.getUsuarioId())) {
            throw new IllegalStateException("Usuario, perfil e configuracoes possuem IDs diferentes");
        }
        return new Usuario(usuario.getId(), usuario.getEmail(), usuario.getStatus(),
                usuario.getCriadoEm(), usuario.getExcluidoEm(),
                PerfilMapper.paraDominio(perfil), configuracoesMapper.paraDominio(configuracoes));
    }
}
