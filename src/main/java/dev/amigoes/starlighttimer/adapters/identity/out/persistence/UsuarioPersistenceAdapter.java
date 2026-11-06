package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioRepositoryPort;
import dev.amigoes.starlighttimer.domain.identity.entities.Usuario;
import dev.amigoes.starlighttimer.domain.identity.exceptions.EmailJaCadastradoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {
    private final UsuarioJpaRepository usuarios;
    private final PerfilJpaRepository perfis;
    private final ConfiguracoesUsuarioJpaRepository configuracoes;
    private final UsuarioMapper usuarioMapper;
    private final PerfilMapper perfilMapper;
    private final ConfiguracoesUsuarioMapper configuracoesMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository usuarios, PerfilJpaRepository perfis,
                                    ConfiguracoesUsuarioJpaRepository configuracoes,
                                    UsuarioMapper usuarioMapper, PerfilMapper perfilMapper,
                                    ConfiguracoesUsuarioMapper configuracoesMapper) {
        this.usuarios = usuarios;
        this.perfis = perfis;
        this.configuracoes = configuracoes;
        this.usuarioMapper = usuarioMapper;
        this.perfilMapper = perfilMapper;
        this.configuracoesMapper = configuracoesMapper;
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarios.existsByEmail(Objects.requireNonNull(email, "O email e obrigatorio"));
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarios.findById(Objects.requireNonNull(id, "O id e obrigatorio"))
                .map(usuario -> usuarioMapper.paraDominio(usuario,
                        perfis.findById(id).orElseThrow(() ->
                                new IllegalStateException("Usuario persistido sem perfil")),
                        configuracoes.findById(id).orElseThrow(() ->
                                new IllegalStateException("Usuario persistido sem configuracoes"))));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void criar(Usuario usuario) {
        validarAgregado(usuario);
        // Mapear tudo antes de escrever: falhas de conversao nao deixam INSERTs pendentes.
        var usuarioEntity = usuarioMapper.paraEntity(usuario);
        var perfilEntity = perfilMapper.paraEntity(usuario.getPerfil());
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

    @Override
    public boolean existeAtivoPorId(UUID id) {
        return usuarios.existsByIdAndStatusAndExcluidoEmIsNull(id, "ACTIVE");
    }
}
