package dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.GanhoXpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface GanhoXpJpaRepository extends JpaRepository<GanhoXpEntity, UUID> {

    @Query(value = """
        INSERT INTO ganho_xp (id, usuario_id, quantidade, origem_sessao, atribuido_em)
            VALUES (:id, :usuarioId, :quantidade, :origemSessao, :atribuidoEm)
            ON CONFLICT (origem_sessao)
                WHERE origem_sessao IS NOT NULL
            DO NOTHING;
    """, nativeQuery = true)
    @Modifying
    int inserirPorSessao(
            @Param("id") UUID id,
            @Param("usuarioId") UUID usuarioId,
            @Param("quantidade") Integer quantidade,
            @Param("origemSessao") UUID origemSessao,
            @Param("atribuidoEm") OffsetDateTime atribuidoEm
    );

    @Query(value = """
        INSERT INTO ganho_xp (id, usuario_id, quantidade, origem_constelacao, atribuido_em)
            VALUES (:id, :usuarioId, :quantidade, :origemConstelacao, :atribuidoEm)
            ON CONFLICT (usuario_id, origem_constelacao)
                WHERE origem_constelacao IS NOT NULL
            DO NOTHING;
    """, nativeQuery = true)
    @Modifying
    int inserirPorConstelacao(
            @Param("id") UUID id,
            @Param("usuarioId") UUID usuarioId,
            @Param("quantidade") Integer quantidade,
            @Param("origemConstelacao") UUID origemConstelacao,
            @Param("atribuidoEm") OffsetDateTime atribuidoEm
    );

    @Query(value = """
        INSERT INTO ganho_xp (id, usuario_id, quantidade, origem_insignia, atribuido_em)
            VALUES (:id, :usuarioId, :quantidade, :origemInsignia, :atribuidoEm)
            ON CONFLICT (usuario_id, origem_insignia)
                WHERE origem_insignia IS NOT NULL
            DO NOTHING;
    """, nativeQuery = true)
    @Modifying
    int inserirPorInsignia(
            @Param("id") UUID id,
            @Param("usuarioId") UUID usuarioId,
            @Param("quantidade") Integer quantidade,
            @Param("origemInsignia") UUID origemInsignia,
            @Param("atribuidoEm") OffsetDateTime atribuidoEm
    );
}
