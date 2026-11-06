package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.app.identity.ports.out.ConfiguracoesUsuarioRepositoryPort;
import dev.amigoes.starlighttimer.domain.social.entities.ConfiguracoesUsuario;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ConfiguracoesUsuarioPersistenceAdapter implements ConfiguracoesUsuarioRepositoryPort {

    private final ConfiguracoesUsuarioJpaRepository repositoryJpa;

    public ConfiguracoesUsuarioPersistenceAdapter(ConfiguracoesUsuarioJpaRepository repositoryJpa) {
        this.repositoryJpa = repositoryJpa;
    }

    @Override
    public ConfiguracoesUsuario salvar(ConfiguracoesUsuario configuracoes) {
        ConfiguracoesUsuarioEntity salva = repositoryJpa.save(IdentityMapper.paraEntidade(configuracoes));
        return IdentityMapper.paraDominio(salva);
    }

    @Override
    public Optional<ConfiguracoesUsuario> buscarPorUsuarioId(UUID usuarioId) {
        return repositoryJpa.findById(usuarioId).map(IdentityMapper::paraDominio);
    }
}