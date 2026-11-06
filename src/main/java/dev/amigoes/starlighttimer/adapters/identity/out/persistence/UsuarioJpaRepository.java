package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    boolean existsByEmail(String email);
}
