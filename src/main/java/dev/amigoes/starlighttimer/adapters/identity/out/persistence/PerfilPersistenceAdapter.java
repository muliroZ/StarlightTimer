package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.app.identity.ports.out.PerfilRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.domain.identity.entities.Perfil;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PerfilPersistenceAdapter implements PerfilRepositoryPort {

    private final PerfilJpaRepository repositoryJpa;
    private final PerfilMapper mapper;

    public PerfilPersistenceAdapter(PerfilJpaRepository repositoryJpa, PerfilMapper mapper) {
        this.repositoryJpa = repositoryJpa;
        this.mapper = mapper;
    }

    @Override
    public Perfil salvar(Perfil perfil) {
        int atualizadas = repositoryJpa.atualizarCampos(perfil.getUsuarioId(), perfil.getNomeExibido(),
                perfil.getAvatar(), perfil.getBio(), perfil.getFusoHorario());
        if (atualizadas == 0) {
            throw new RecursoNaoEncontradoException("Perfil não encontrado para o usuário informado.");
        }
        return repositoryJpa.findById(perfil.getUsuarioId())
                .map(mapper::paraDominio)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado para o usuário informado."));
    }

    @Override
    public Optional<Perfil> buscarPorUsuarioId(UUID usuarioId) {
        return repositoryJpa.findById(usuarioId).map(mapper::paraDominio);
    }
}
