package dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.ConstelacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ConstelacaoJpaRepository extends JpaRepository<ConstelacaoEntity, UUID> {}
