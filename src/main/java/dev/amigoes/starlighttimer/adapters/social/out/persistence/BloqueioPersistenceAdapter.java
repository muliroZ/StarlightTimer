package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.app.social.ports.out.BloqueioRepositoryPort;
import dev.amigoes.starlighttimer.domain.social.entities.Bloqueio;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
}
