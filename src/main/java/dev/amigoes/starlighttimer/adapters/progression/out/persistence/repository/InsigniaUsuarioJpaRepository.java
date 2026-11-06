package dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.InsigniaUsuarioEntity;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.InsigniaUsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.UUID;

@Repository
public interface InsigniaUsuarioJpaRepository extends JpaRepository<InsigniaUsuarioEntity, InsigniaUsuarioId> {
    @Modifying
    @Query(value = """
            INSERT INTO insignia_usuario (usuario_id, insignia_id, contagem_progresso, desbloqueado_em)
            VALUES (:usuarioId, :insigniaId, :contagem, :desbloqueadoEm)
            ON CONFLICT (usuario_id, insignia_id) DO UPDATE SET
                contagem_progresso = GREATEST(insignia_usuario.contagem_progresso, EXCLUDED.contagem_progresso),
                desbloqueado_em = COALESCE(insignia_usuario.desbloqueado_em, EXCLUDED.desbloqueado_em)
            """, nativeQuery = true)
    int salvarMelhorProgresso(@Param("usuarioId") UUID usuarioId,
                             @Param("insigniaId") UUID insigniaId,
                             @Param("contagem") int contagem,
                             @Param("desbloqueadoEm") Instant desbloqueadoEm);
}
