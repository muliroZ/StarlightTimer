package dev.amigoes.starlighttimer.adapters.session.out.persistence;

import dev.amigoes.starlighttimer.domain.session.enums.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoEventoJpaRepository extends JpaRepository<SessaoEventoEntity, UUID> {

  Optional<SessaoEventoEntity> findFirstBySessaoIdAndTipoEventoOrderByOcorridoEmDesc(UUID sessaoId, TipoEvento tipoEvento);
}