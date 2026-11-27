package dev.amigoes.starlighttimer.core.progression.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimiteNivelRepository extends JpaRepository<LimiteNivelEntity, Integer> {
}
