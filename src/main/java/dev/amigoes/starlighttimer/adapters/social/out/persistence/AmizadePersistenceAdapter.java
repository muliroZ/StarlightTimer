package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.app.social.ports.out.AmizadeRepositoryPort;
import dev.amigoes.starlighttimer.domain.social.entities.Amizade;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
}
