package dev.amigoes.starlighttimer.adapters.session.in.web;

import dev.amigoes.starlighttimer.app.session.ports.in.GerenciarSessaoUseCase;
import dev.amigoes.starlighttimer.app.session.ports.in.AcaoSessao;
import dev.amigoes.starlighttimer.app.session.usecases.TransicoesSessaoService;
import dev.amigoes.starlighttimer.domain.session.exceptions.SessaoAtivaExistenteException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
public class SessaoController {
    private final GerenciarSessaoUseCase sessoes;
    private final TransicoesSessaoService transicoes;

    public SessaoController(GerenciarSessaoUseCase sessoes, TransicoesSessaoService transicoes) {
        this.sessoes = sessoes;
        this.transicoes = transicoes;
    }

    @PostMapping("/api/v1/sessoes")
    public ResponseEntity<SessaoResponse> iniciar(@Valid @RequestBody IniciarSessaoRequest request) {
        try {
            var sessao = sessoes.iniciar(request.tipo(), request.duracaoPlanejada(), request.reportadoEm());
            return ResponseEntity.status(201).body(SessaoResponse.deDominio(sessao, transicoes.horarioServidor(), null));
        } catch (DataIntegrityViolationException ex) {
            sessoes.buscarAtiva().ifPresent(ativa -> {
                throw new SessaoAtivaExistenteException(ativa.getId());
            });
            throw ex;
        }
    }

    @PatchMapping("/api/v1/sessoes/{id}")
    public SessaoResponse transicionar(@PathVariable UUID id, @Valid @RequestBody TransicionarSessaoRequest request) {
        AcaoSessao acao = switch (request.status()) {
            case PAUSED -> AcaoSessao.PAUSAR;
            case RUNNING -> AcaoSessao.RETOMAR;
            case ABANDONED -> AcaoSessao.ABANDONAR;
            case COMPLETED -> AcaoSessao.CONCLUIR;
        };
        var resultado = sessoes.transicionar(id, acao, request.reportadoEm());
        return SessaoResponse.deDominio(resultado.sessao(), transicoes.horarioServidor(), resultado.recompensa());
    }

    @GetMapping("/api/v1/me/sessao-ativa")
    public ResponseEntity<SessaoResponse> ativa() {
        return sessoes.buscarAtiva()
                .map(sessao -> ResponseEntity.ok(SessaoResponse.deDominio(sessao, transicoes.horarioServidor(), null)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/api/v1/sessoes")
    public PaginaSessoesResponse historico(@RequestParam(defaultValue = "0") @Min(0) int pagina,
                                           @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamanho,
                                           @RequestParam(required = false) LocalDate dataInicio,
                                           @RequestParam(required = false) LocalDate dataFim) {
        List<SessaoResponse> itens = sessoes.listarHistorico(dataInicio, dataFim, tamanho + 1,
                        Math.multiplyExact(pagina, tamanho)).stream()
                .map(sessao -> SessaoResponse.deDominio(sessao, transicoes.horarioServidor(), null)).toList();
        return new PaginaSessoesResponse(itens.stream().limit(tamanho).toList(), pagina, tamanho, itens.size() > tamanho);
    }

    public record PaginaSessoesResponse(List<SessaoResponse> itens, int pagina, int tamanho, boolean proximaPagina) {}
}
