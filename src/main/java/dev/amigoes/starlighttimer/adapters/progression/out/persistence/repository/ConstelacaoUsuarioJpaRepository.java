package dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.ConstelacaoUsuarioEntity;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.ConstelacaoUsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.UUID;

public interface ConstelacaoUsuarioJpaRepository extends JpaRepository<ConstelacaoUsuarioEntity, ConstelacaoUsuarioId> {
    @Modifying
    @Query(value = """
            INSERT INTO constelacao_usuario (constelacao_id, usuario_id, desbloqueado_em)
            VALUES (:constelacaoId, :usuarioId, :desbloqueadoEm)
            ON CONFLICT (constelacao_id, usuario_id) DO NOTHING
            """, nativeQuery = true)
    int inserirDesbloqueio(@Param("constelacaoId") UUID constelacaoId,
                           @Param("usuarioId") UUID usuarioId,
                           @Param("desbloqueadoEm") Instant desbloqueadoEm);
}
