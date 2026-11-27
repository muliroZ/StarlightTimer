package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.records.social.Bloqueio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
public interface BloqueioRepository extends JpaRepository<ListaBloqueioEntity, BloqueioId> {
    @Query("""
            select b from ListaBloqueioEntity b
            where (b.id.usuarioId = :primeiro and b.id.bloqueadoId = :segundo)
               or (b.id.usuarioId = :segundo and b.id.bloqueadoId = :primeiro)
            """)
    List<ListaBloqueioEntity> consultarEntre(@Param("primeiro") UUID primeiro, @Param("segundo") UUID segundo);

    default List<Bloqueio> buscarEntre(UUID primeiro, UUID segundo) {
        return consultarEntre(primeiro, segundo).stream().map(BloqueioMapper::paraDominio).toList();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    default void criar(UUID usuarioId, UUID bloqueadoId) {
        var id = new BloqueioId(usuarioId, bloqueadoId);
        if (!existsById(id)) {
            saveAndFlush(new ListaBloqueioEntity(id));
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    default void remover(UUID usuarioId, UUID bloqueadoId) {
        var id = new BloqueioId(usuarioId, bloqueadoId);
        if (existsById(id)) {
            deleteById(id);
        }
    }
}
