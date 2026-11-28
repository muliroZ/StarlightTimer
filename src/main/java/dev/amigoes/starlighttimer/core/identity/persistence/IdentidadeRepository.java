package dev.amigoes.starlighttimer.core.identity.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdentidadeRepository extends JpaRepository<IdentidadeEntity, UUID> {
    Optional<IdentidadeEntity> findByUsuarioIdAndProvedor(UUID usuarioId, String provedor);
}
