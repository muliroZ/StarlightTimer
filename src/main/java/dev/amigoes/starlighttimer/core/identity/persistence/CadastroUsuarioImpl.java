package dev.amigoes.starlighttimer.core.identity.persistence;

import dev.amigoes.starlighttimer.core.identity.domain.EmailJaCadastradoException;
import dev.amigoes.starlighttimer.core.identity.domain.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

class CadastroUsuarioImpl implements CadastroUsuario {
    private final UsuarioMapper usuarioMapper;
    private final ConfiguracoesUsuarioMapper configuracoesMapper;

    @PersistenceContext
    private EntityManager entityManager;

    CadastroUsuarioImpl(UsuarioMapper usuarioMapper,
                        ConfiguracoesUsuarioMapper configuracoesMapper) {
        this.usuarioMapper = usuarioMapper;
        this.configuracoesMapper = configuracoesMapper;
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        var usuario = entityManager.find(UsuarioEntity.class, Objects.requireNonNull(id, "O id e obrigatorio"));
        if (usuario == null) return Optional.empty();

        var perfil = entityManager.find(PerfilEntity.class, id);
        if (perfil == null) throw new IllegalStateException("Usuario persistido sem perfil");

        var config = entityManager.find(ConfiguracoesUsuarioEntity.class, id);
        if (config == null) throw new IllegalStateException("Usuario persistido sem configuracoes");

        return Optional.of(usuarioMapper.paraDominio(usuario, perfil, config));
    }

    @Override
    public void criar(Usuario usuario) {
        validarAgregado(usuario);
        // Mapear tudo antes de escrever: falhas de conversao nao deixam INSERTs pendentes.
        var usuarioEntity = usuarioMapper.paraEntity(usuario);
        var perfilEntity = PerfilMapper.paraEntity(usuario.getPerfil());
        var configuracoesEntity = configuracoesMapper.paraEntity(usuario.getConfiguracoes());
        try {
            // persist exige criacao; save com UUID atribuido poderia executar merge.
            entityManager.persist(usuarioEntity);
            entityManager.flush(); // garante a FK antes de inserir os dependentes
            entityManager.persist(perfilEntity);
            entityManager.persist(configuracoesEntity);
            entityManager.flush(); // antecipa falhas para dentro deste tratamento
        } catch (PersistenceException ex) {
            if (violouEmailUnico(ex)) {
                throw new EmailJaCadastradoException(ex);
            }
            throw ex;
        }
    }

    private void validarAgregado(Usuario usuario) {
        Objects.requireNonNull(usuario, "O usuario e obrigatorio");
        Objects.requireNonNull(usuario.getId(), "O id e obrigatorio");
        Objects.requireNonNull(usuario.getPerfil(), "O perfil e obrigatorio");
        Objects.requireNonNull(usuario.getConfiguracoes(), "As configuracoes sao obrigatorias");
        if (!usuario.getId().equals(usuario.getPerfil().getUsuarioId())
                || !usuario.getId().equals(usuario.getConfiguracoes().getUsuarioId())) {
            throw new IllegalArgumentException("Usuario, perfil e configuracoes possuem IDs diferentes");
        }
    }

    private boolean violouEmailUnico(Throwable erro) {
        for (Throwable causa = erro; causa != null; causa = causa.getCause()) {
            if (causa instanceof ConstraintViolationException violacao
                    && "23505".equals(violacao.getSQLState())
                    && "usuario_email_key".equals(violacao.getConstraintName())) {
                return true;
            }
        }
        return false;
    }
}
