package dev.amigoes.starlighttimer.core.progression.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InsigniaRepository extends JpaRepository<InsigniaEntity, UUID> {
}
