package dev.amigoes.starlighttimer.core.progression.persistence;

import dev.amigoes.starlighttimer.core.progression.domain.ProgressoInsignia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsigniaUsuarioRepository extends JpaRepository<InsigniaUsuarioEntity, InsigniaUsuarioId> {
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

    default Optional<ProgressoInsignia> buscarProgresso(UUID usuarioId, UUID insigniaId) {
        return findById(new InsigniaUsuarioId(usuarioId, insigniaId))
                .map(CatalogoMapper::paraProgresso);
    }

    default void registrarProgresso(ProgressoInsignia progresso) {
        salvarMelhorProgresso(progresso.getUsuarioId(),
                progresso.getInsigniaId(), progresso.getContagemProgresso(), progresso.getDesbloqueadoEm());
    }
}
