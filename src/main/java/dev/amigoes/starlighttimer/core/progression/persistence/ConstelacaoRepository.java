package dev.amigoes.starlighttimer.core.progression.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConstelacaoRepository extends JpaRepository<ConstelacaoEntity, UUID> {}
