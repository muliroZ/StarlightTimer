package dev.amigoes.starlighttimer.app.session.ports.out;

import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.entities.SessaoEvento;
import dev.amigoes.starlighttimer.domain.session.enums.TipoEvento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessaoRepositoryPort {
  Sessao salvarComEvento(Sessao sessao, SessaoEvento evento);

  Sessao salvar(Sessao sessao);

  Optional<Sessao> buscarPorIdEUsuario(UUID id, UUID usuarioId);

  Optional<Sessao> buscarAtivaPorUsuario(UUID usuarioId);

  Optional<SessaoEvento> buscarUltimoEventoPorTipo(UUID sessaoId, TipoEvento tipoEvento);

  List<Sessao> listarHistorico(UUID usuarioId, int limit, int offset);

  List<LocalDate> listarDiasComFocoConcluido(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim);
}


