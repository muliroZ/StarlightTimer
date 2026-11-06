package dev.amigoes.starlighttimer.app.social.ports.out;

import dev.amigoes.starlighttimer.domain.social.entities.Amizade;

import java.util.Optional;
import java.util.UUID;

public interface AmizadeRepositoryPort {
    Optional<Amizade> buscarEntre(UUID primeiro, UUID segundo);
}
