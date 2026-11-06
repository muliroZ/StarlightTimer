package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface PerfilJpaRepository extends JpaRepository<PerfilEntity, UUID> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PerfilEntity p SET p.nomeExibido = ?2, p.avatar = ?3, p.bio = ?4, p.fusoHorario = ?5 WHERE p.usuarioId = ?1")
    int atualizarCampos(UUID usuarioId, String nomeExibido, String avatar, String bio, String fusoHorario);
}
