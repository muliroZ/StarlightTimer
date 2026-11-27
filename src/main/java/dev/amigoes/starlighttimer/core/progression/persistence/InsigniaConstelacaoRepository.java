package dev.amigoes.starlighttimer.core.progression.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InsigniaConstelacaoRepository extends JpaRepository<InsigniaConstelacaoEntity, InsigniaConstelacaoId> {
    List<InsigniaConstelacaoEntity> findByIdConstelacaoId(UUID constelacaoId);
}
