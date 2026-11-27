package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.records.social.Amizade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
public interface AmizadeRepository extends JpaRepository<AmizadeEntity, UUID> {
    @Query("""
            select a from AmizadeEntity a
            where (a.usuario1Id = :primeiro and a.usuario2Id = :segundo)
               or (a.usuario1Id = :segundo and a.usuario2Id = :primeiro)
            """)
    Optional<AmizadeEntity> consultarEntre(@Param("primeiro") UUID primeiro, @Param("segundo") UUID segundo);

    @Query("select a from AmizadeEntity a where a.usuario1Id = :usuarioId or a.usuario2Id = :usuarioId order by a.estabelecidaEm desc")
    List<AmizadeEntity> consultarPorUsuario(@Param("usuarioId") UUID usuarioId);

    default Optional<Amizade> buscarEntre(UUID primeiro, UUID segundo) {
        return consultarEntre(primeiro, segundo).map(AmizadeMapper::paraDominio);
    }

    default Optional<Amizade> buscarPorId(UUID id) {
        return findById(id).map(AmizadeMapper::paraDominio);
    }

    default List<Amizade> listarPorUsuario(UUID usuarioId) {
        return consultarPorUsuario(usuarioId).stream().map(AmizadeMapper::paraDominio).toList();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    default void remover(UUID id) {
        deleteById(id);
        flush();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    default void removerEntre(UUID primeiro, UUID segundo) {
        consultarEntre(primeiro, segundo).ifPresent(this::delete);
        flush();
    }
}
