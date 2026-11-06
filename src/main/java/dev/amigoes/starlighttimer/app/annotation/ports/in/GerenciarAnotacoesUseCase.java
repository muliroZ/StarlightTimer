package dev.amigoes.starlighttimer.app.annotation.ports.in;

import dev.amigoes.starlighttimer.domain.annotation.Anotacao;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GerenciarAnotacoesUseCase {
  Anotacao criar(UUID usuarioId, CriarAnotacaoCommand comando);
  List<Anotacao> listar(UUID usuarioId, UUID sessaoId, LocalDate data);
  Anotacao buscarPorId(UUID usuarioId, UUID anotacaoId);
  Anotacao editar(UUID usuarioId, UUID anotacaoId, EditarAnotacaoCommand comando);
  void apagar(UUID usuarioId, UUID anotacaoId);
}