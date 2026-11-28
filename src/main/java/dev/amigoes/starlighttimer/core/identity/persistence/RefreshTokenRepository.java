package dev.amigoes.starlighttimer.core.identity.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshTokenEntity t set t.revogadoEm = :agora where t.id = :id and t.revogadoEm is null")
    int revogarSeAtivo(@Param("id") UUID id, @Param("agora") Instant agora);
}
