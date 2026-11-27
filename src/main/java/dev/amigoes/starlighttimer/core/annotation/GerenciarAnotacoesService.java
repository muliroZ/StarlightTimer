package dev.amigoes.starlighttimer.core.annotation;

import dev.amigoes.starlighttimer.core.annotation.domain.Anotacao;
import dev.amigoes.starlighttimer.core.annotation.persistence.AnotacaoRepository;
import dev.amigoes.starlighttimer.core.session.usecase.VerificarPosseSessaoService;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.exceptions.RegraDeNegocioException;
import dev.amigoes.starlighttimer.records.annotation.CriarAnotacaoCommand;
import dev.amigoes.starlighttimer.records.annotation.EditarAnotacaoCommand;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class GerenciarAnotacoesService {

  private final AnotacaoRepository anotacaoRepository;
  private final VerificarPosseSessaoService verificarPosseSessao;
  private final Clock clock;

  public GerenciarAnotacoesService(AnotacaoRepository anotacaoRepository,
                                   VerificarPosseSessaoService verificarPosseSessao,
                                   Clock clock) {
    this.anotacaoRepository = Objects.requireNonNull(anotacaoRepository);
    this.verificarPosseSessao = Objects.requireNonNull(verificarPosseSessao);
    this.clock = Objects.requireNonNull(clock);
  }

  @Transactional
  public Anotacao criar(UUID usuarioId, CriarAnotacaoCommand comando) {
    Objects.requireNonNull(usuarioId, "ID do usuario obrigatorio");
    Objects.requireNonNull(comando, "Comando obrigatorio");

    if (comando.sessaoId() != null) {
      verificarPosseSessao.verificar(usuarioId, comando.sessaoId());
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

  @Transactional(readOnly = true)
  public List<Anotacao> listar(UUID usuarioId, UUID sessaoId, LocalDate data) {
    Objects.requireNonNull(usuarioId, "ID do usuario obrigatorio");

    if (sessaoId != null && data != null) {
      throw new RegraDeNegocioException("Informe apenas sessaoId ou data.");
    }

    if (sessaoId != null) {
      verificarPosseSessao.verificar(usuarioId, sessaoId);
      return anotacaoRepository.buscarPorUsuarioESessao(usuarioId, sessaoId);
    }

    if (data != null) {
      return anotacaoRepository.buscarPorUsuarioEData(usuarioId, data);
    }

    throw new RegraDeNegocioException("E necessario informar o filtro de sessaoId ou data.");
  }

  @Transactional(readOnly = true)
  public Anotacao buscarPorId(UUID usuarioId, UUID anotacaoId) {
    return buscarValidandoPosse(usuarioId, anotacaoId);
  }

  @Transactional
  public Anotacao editar(UUID usuarioId, UUID anotacaoId, EditarAnotacaoCommand comando) {
    Objects.requireNonNull(comando, "Comando obrigatorio");
    Anotacao anotacao = buscarValidandoPosse(usuarioId, anotacaoId);

    anotacao.atualizarConteudo(comando.conteudo(), clock.instant());
    return anotacaoRepository.salvar(anotacao);
  }

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
