package dev.amigoes.starlighttimer.controller;

import dev.amigoes.starlighttimer.core.annotation.GerenciarAnotacoesService;
import dev.amigoes.starlighttimer.core.annotation.domain.Anotacao;
import dev.amigoes.starlighttimer.core.identity.usecase.ConsultarIdentidadeService;
import dev.amigoes.starlighttimer.records.annotation.AnotacaoResponse;
import dev.amigoes.starlighttimer.records.annotation.CriarAnotacaoCommand;
import dev.amigoes.starlighttimer.records.annotation.CriarAnotacaoRequest;
import dev.amigoes.starlighttimer.records.annotation.EditarAnotacaoCommand;
import dev.amigoes.starlighttimer.records.annotation.EditarAnotacaoRequest;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/anotacoes")
public class AnotacaoController {

  private final GerenciarAnotacoesService useCase;
  private final ConsultarIdentidadeService identidade;

  public AnotacaoController(GerenciarAnotacoesService useCase, ConsultarIdentidadeService identidade) {
    this.useCase = useCase;
    this.identidade = identidade;
  }

  @PostMapping
  public ResponseEntity<AnotacaoResponse> criar(@Valid @RequestBody CriarAnotacaoRequest request) {
    UUID usuarioId = identidade.obterUsuarioAtual();
    CriarAnotacaoCommand comando = new CriarAnotacaoCommand(
        request.sessaoId(),
        request.conteudo(),
        request.dataAnotacao()
    );
    Anotacao criada = useCase.criar(usuarioId, comando);
    return ResponseEntity.status(HttpStatus.CREATED).body(AnotacaoResponse.de(criada));
  }

  @GetMapping
  public ResponseEntity<List<AnotacaoResponse>> listar(
      @RequestParam(required = false) UUID sessaoId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
    UUID usuarioId = identidade.obterUsuarioAtual();
    List<AnotacaoResponse> responses = useCase.listar(usuarioId, sessaoId, data)
        .stream()
        .map(AnotacaoResponse::de)
        .toList();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AnotacaoResponse> buscarPorId(@PathVariable UUID id) {
    UUID usuarioId = identidade.obterUsuarioAtual();
    Anotacao anotacao = useCase.buscarPorId(usuarioId, id);
    return ResponseEntity.ok(AnotacaoResponse.de(anotacao));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<AnotacaoResponse> editar(
      @PathVariable UUID id,
      @Valid @RequestBody EditarAnotacaoRequest request) {
    UUID usuarioId = identidade.obterUsuarioAtual();
    EditarAnotacaoCommand comando = new EditarAnotacaoCommand(request.conteudo());
    Anotacao atualizada = useCase.editar(usuarioId, id, comando);
    return ResponseEntity.ok(AnotacaoResponse.de(atualizada));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> apagar(@PathVariable UUID id) {
    UUID usuarioId = identidade.obterUsuarioAtual();
    useCase.apagar(usuarioId, id);
    return ResponseEntity.noContent().build();
  }
}
