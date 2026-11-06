package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.app.identity.ports.out.ConfiguracoesUsuarioRepositoryPort;
import dev.amigoes.starlighttimer.domain.identity.entities.ConfiguracoesUsuario;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ConfiguracoesUsuarioPersistenceAdapter implements ConfiguracoesUsuarioRepositoryPort {

    private final ConfiguracoesUsuarioJpaRepository repositoryJpa;
    private final ConfiguracoesUsuarioMapper mapper;

    public ConfiguracoesUsuarioPersistenceAdapter(ConfiguracoesUsuarioJpaRepository repositoryJpa,
                                                  ConfiguracoesUsuarioMapper mapper) {
        this.repositoryJpa = repositoryJpa;
        this.mapper = mapper;
    }

    @Override
    public ConfiguracoesUsuario salvar(ConfiguracoesUsuario configuracoes) {
        ConfiguracoesUsuarioEntity salva = repositoryJpa.save(mapper.paraEntity(configuracoes));
        return mapper.paraDominio(salva);
    }

    @Override
    public Optional<ConfiguracoesUsuario> buscarPorUsuarioId(UUID usuarioId) {
        return repositoryJpa.findById(usuarioId).map(mapper::paraDominio);
    }
}
