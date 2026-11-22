package dev.amigoes.starlighttimer.adapters.shared.out.outbox.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findTop100ByPublicadoEmNullOrderByCriadoEmAsc();
}
