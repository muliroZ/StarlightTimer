package dev.amigoes.starlighttimer.app.annotation.usecases;

import dev.amigoes.starlighttimer.app.annotation.ports.in.CriarAnotacaoCommand;
import dev.amigoes.starlighttimer.app.annotation.ports.in.EditarAnotacaoCommand;
import dev.amigoes.starlighttimer.app.annotation.ports.in.GerenciarAnotacoesUseCase;
import dev.amigoes.starlighttimer.app.annotation.ports.out.AnotacaoRepositoryPort;
import dev.amigoes.starlighttimer.app.session.ports.out.SessaoRepositoryPort;
import dev.amigoes.starlighttimer.domain.annotation.Anotacao;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RegraDeNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class GerenciarAnotacoesService implements GerenciarAnotacoesUseCase {

  private final AnotacaoRepositoryPort anotacaoRepository;
  private final SessaoRepositoryPort sessaoRepository;
  private final Clock clock;

  public GerenciarAnotacoesService(AnotacaoRepositoryPort anotacaoRepository,
                                   SessaoRepositoryPort sessaoRepository,
                                   Clock clock) {
    this.anotacaoRepository = Objects.requireNonNull(anotacaoRepository);
    this.sessaoRepository = Objects.requireNonNull(sessaoRepository);
    this.clock = Objects.requireNonNull(clock);
  }

  @Override
  @Transactional
  public Anotacao criar(UUID usuarioId, CriarAnotacaoCommand comando) {
    Objects.requireNonNull(usuarioId, "ID do usuario obrigatorio");
    Objects.requireNonNull(comando, "Comando obrigatorio");

    if (comando.sessaoId() != null) {
      sessaoRepository.buscarPorIdEUsuario(comando.sessaoId(), usuarioId)
          .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));
    }

    Instant agora = clock.instant();
    Anotacao nova = new Anotacao(
        UUID.randomUUID(),
        usuarioId,
        comando.sessaoId(),
        comando.conteudo(),
        comando.dataAnotacao(),
        agora,
        agora
    );

    return anotacaoRepository.salvar(nova);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Anotacao> listar(UUID usuarioId, UUID sessaoId, LocalDate data) {
    Objects.requireNonNull(usuarioId, "ID do usuario obrigatorio");

    if (sessaoId != null) {
      sessaoRepository.buscarPorIdEUsuario(sessaoId, usuarioId)
          .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));
      return anotacaoRepository.buscarPorUsuarioESessao(usuarioId, sessaoId);
    }

    if (data != null) {
      return anotacaoRepository.buscarPorUsuarioEData(usuarioId, data);
    }

    throw new RegraDeNegocioException("E necessario informar o filtro de sessaoId ou data.");
  }

  @Override
  @Transactional(readOnly = true)
  public Anotacao buscarPorId(UUID usuarioId, UUID anotacaoId) {
    return buscarValidandoPosse(usuarioId, anotacaoId);
  }

  @Override
  @Transactional
  public Anotacao editar(UUID usuarioId, UUID anotacaoId, EditarAnotacaoCommand comando) {
    Objects.requireNonNull(comando, "Comando obrigatorio");
    Anotacao anotacao = buscarValidandoPosse(usuarioId, anotacaoId);

    anotacao.atualizarConteudo(comando.conteudo(), clock.instant());
    return anotacaoRepository.salvar(anotacao);
  }

  @Override
  @Transactional
  public void apagar(UUID usuarioId, UUID anotacaoId) {
    buscarValidandoPosse(usuarioId, anotacaoId);
    anotacaoRepository.remover(anotacaoId);
  }

  private Anotacao buscarValidandoPosse(UUID usuarioId, UUID anotacaoId) {
    Objects.requireNonNull(usuarioId, "ID do usuario obrigatorio");
    Objects.requireNonNull(anotacaoId, "ID da anotacao obrigatorio");

    Anotacao anotacao = anotacaoRepository.buscarPorId(anotacaoId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Anotacao nao encontrada"));

    if (!anotacao.getUsuarioId().equals(usuarioId)) {
      throw new RecursoNaoEncontradoException("Anotacao nao encontrada");
    }

    return anotacao;
  }
}