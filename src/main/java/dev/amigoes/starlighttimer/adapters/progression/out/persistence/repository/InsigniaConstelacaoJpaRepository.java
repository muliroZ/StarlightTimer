package dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.InsigniaConstelacaoEntity;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.InsigniaConstelacaoId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InsigniaConstelacaoJpaRepository extends JpaRepository<InsigniaConstelacaoEntity, InsigniaConstelacaoId> {
    List<InsigniaConstelacaoEntity> findByIdConstelacaoId(UUID constelacaoId);
}
