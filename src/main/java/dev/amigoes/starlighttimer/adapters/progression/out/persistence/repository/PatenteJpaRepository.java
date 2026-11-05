package dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.PatenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatenteJpaRepository extends JpaRepository<PatenteEntity, UUID> {
}
