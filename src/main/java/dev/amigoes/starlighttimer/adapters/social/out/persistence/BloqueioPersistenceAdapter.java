package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.app.social.ports.out.BloqueioRepositoryPort;
import dev.amigoes.starlighttimer.domain.social.entities.Bloqueio;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class BloqueioPersistenceAdapter implements BloqueioRepositoryPort {
    private final ListaBloqueioJpaRepository bloqueios;
    private final BloqueioMapper mapper;

    public BloqueioPersistenceAdapter(ListaBloqueioJpaRepository bloqueios, BloqueioMapper mapper) {
        this.bloqueios = bloqueios;
        this.mapper = mapper;
    }

    @Override
    public List<Bloqueio> buscarEntre(UUID primeiro, UUID segundo) {
        return bloqueios.buscarEntre(primeiro, segundo).stream().map(mapper::paraDominio).toList();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void criar(UUID usuarioId, UUID bloqueadoId) {
        var id = new BloqueioId(usuarioId, bloqueadoId);
        if (!bloqueios.existsById(id)) {
            bloqueios.saveAndFlush(new ListaBloqueioEntity(id));
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void remover(UUID usuarioId, UUID bloqueadoId) {
        var id = new BloqueioId(usuarioId, bloqueadoId);
        if (bloqueios.existsById(id)) {
            bloqueios.deleteById(id);
        }
    }
}
