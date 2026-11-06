package dev.amigoes.starlighttimer.app.annotation.ports.out;

import dev.amigoes.starlighttimer.domain.annotation.Anotacao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnotacaoRepositoryPort {
    Anotacao salvar(Anotacao anotacao);
    Optional<Anotacao> buscarPorId(UUID id);
    List<Anotacao> buscarPorUsuarioESessao(UUID usuarioId, UUID sessaoId);
    List<Anotacao> buscarPorUsuarioEData(UUID usuarioId, LocalDate dataAnotacao);
    void remover(UUID id);
}