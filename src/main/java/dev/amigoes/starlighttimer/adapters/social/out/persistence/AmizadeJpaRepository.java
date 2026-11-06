package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface AmizadeJpaRepository extends JpaRepository<AmizadeEntity, UUID> {
    @Query("""
            select a from AmizadeEntity a
            where (a.usuario1Id = :primeiro and a.usuario2Id = :segundo)
               or (a.usuario1Id = :segundo and a.usuario2Id = :primeiro)
            """)
    Optional<AmizadeEntity> buscarEntre(@Param("primeiro") UUID primeiro, @Param("segundo") UUID segundo);

    @Query("select a from AmizadeEntity a where a.usuario1Id = :usuarioId or a.usuario2Id = :usuarioId order by a.estabelecidaEm desc")
    List<AmizadeEntity> listarPorUsuario(@Param("usuarioId") UUID usuarioId);
}
