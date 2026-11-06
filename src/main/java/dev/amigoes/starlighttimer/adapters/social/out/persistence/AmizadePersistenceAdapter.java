package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.app.social.ports.out.AmizadeRepositoryPort;
import dev.amigoes.starlighttimer.domain.social.entities.Amizade;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class AmizadePersistenceAdapter implements AmizadeRepositoryPort {
    private final AmizadeJpaRepository amizades;
    private final AmizadeMapper mapper;

    public AmizadePersistenceAdapter(AmizadeJpaRepository amizades, AmizadeMapper mapper) {
        this.amizades = amizades;
        this.mapper = mapper;
    }

    @Override
    public Optional<Amizade> buscarEntre(UUID primeiro, UUID segundo) {
        return amizades.buscarEntre(primeiro, segundo).map(mapper::paraDominio);
    }

    @Override
    public Optional<Amizade> buscarPorId(UUID id) {
        return amizades.findById(id).map(mapper::paraDominio);
    }

    @Override
    public List<Amizade> listarPorUsuario(UUID usuarioId) {
        return amizades.listarPorUsuario(usuarioId).stream().map(mapper::paraDominio).toList();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void remover(UUID id) {
        amizades.deleteById(id);
        amizades.flush();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void removerEntre(UUID primeiro, UUID segundo) {
        amizades.buscarEntre(primeiro, segundo).ifPresent(amizades::delete);
        amizades.flush();
    }
}
