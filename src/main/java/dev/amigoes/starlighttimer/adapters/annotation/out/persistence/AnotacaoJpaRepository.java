package dev.amigoes.starlighttimer.adapters.annotation.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnotacaoJpaRepository extends JpaRepository<AnotacaoEntity, UUID> {
    List<AnotacaoEntity> findByUsuarioIdAndSessaoId(UUID usuarioId, UUID sessaoId);
    List<AnotacaoEntity> findByUsuarioIdAndDataAnotacao(UUID usuarioId, LocalDate dataAnotacao);
}