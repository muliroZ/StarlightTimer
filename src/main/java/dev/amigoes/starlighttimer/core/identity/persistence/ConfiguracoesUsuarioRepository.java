package dev.amigoes.starlighttimer.core.identity.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConfiguracoesUsuarioRepository extends JpaRepository<ConfiguracoesUsuarioEntity, UUID> {
}
