package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.app.identity.ports.out.PerfilRepositoryPort;
import dev.amigoes.starlighttimer.domain.social.entities.Perfil;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PerfilPersistenceAdapter implements PerfilRepositoryPort {

    private final PerfilJpaRepository repositoryJpa;

    public PerfilPersistenceAdapter(PerfilJpaRepository repositoryJpa) {
        this.repositoryJpa = repositoryJpa;
    }

    @Override
    public Perfil salvar(Perfil perfil) {
        PerfilEntity salva = repositoryJpa.save(IdentityMapper.paraEntidade(perfil));
        return IdentityMapper.paraDominio(salva);
    }

    @Override
    public Optional<Perfil> buscarPorUsuarioId(UUID usuarioId) {
        return repositoryJpa.findById(usuarioId).map(IdentityMapper::paraDominio);
    }
}