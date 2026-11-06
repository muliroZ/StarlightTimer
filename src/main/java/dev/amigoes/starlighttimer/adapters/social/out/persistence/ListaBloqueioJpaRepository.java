package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ListaBloqueioJpaRepository extends JpaRepository<ListaBloqueioEntity, BloqueioId> {
    @Query("""
            select b from ListaBloqueioEntity b
            where (b.id.usuarioId = :primeiro and b.id.bloqueadoId = :segundo)
               or (b.id.usuarioId = :segundo and b.id.bloqueadoId = :primeiro)
            """)
    List<ListaBloqueioEntity> buscarEntre(@Param("primeiro") UUID primeiro, @Param("segundo") UUID segundo);
}
