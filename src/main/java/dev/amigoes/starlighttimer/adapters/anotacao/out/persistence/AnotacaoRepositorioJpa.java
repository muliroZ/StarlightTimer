package dev.amigoes.starlighttimer.adapters.anotacao.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnotacaoRepositorioJpa extends JpaRepository<AnotacaoEntidade, UUID> {
    List<AnotacaoEntidade> findByUsuarioIdAndSessaoId(UUID usuarioId, UUID sessaoId);
    List<AnotacaoEntidade> findByUsuarioIdAndDataAnotacao(UUID usuarioId, LocalDate dataAnotacao);
}