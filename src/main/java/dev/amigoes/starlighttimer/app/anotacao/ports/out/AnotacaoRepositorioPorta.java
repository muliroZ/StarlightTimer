package dev.amigoes.starlighttimer.app.anotacao.ports.out;

import dev.amigoes.starlighttimer.domain.anotacao.Anotacao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnotacaoRepositorioPorta {
    Anotacao salvar(Anotacao anotacao);
    Optional<Anotacao> buscarPorId(UUID id);
    List<Anotacao> buscarPorUsuarioESessao(UUID usuarioId, UUID sessaoId);
    List<Anotacao> buscarPorUsuarioEData(UUID usuarioId, LocalDate dataAnotacao);
}