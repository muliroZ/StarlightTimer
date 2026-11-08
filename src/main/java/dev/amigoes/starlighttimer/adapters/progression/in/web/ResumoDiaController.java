package dev.amigoes.starlighttimer.adapters.progression.in.web;

import dev.amigoes.starlighttimer.app.progression.dto.ResumoDiaDto;
import dev.amigoes.starlighttimer.app.progression.ports.in.ObterResumoDiaUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/resumo-dia")
public class ResumoDiaController {

    private final ObterResumoDiaUseCase obterResumoDiaUseCase;

    public ResumoDiaController(ObterResumoDiaUseCase obterResumoDiaUseCase) {
        this.obterResumoDiaUseCase = obterResumoDiaUseCase;
    }

    @GetMapping
    public ResponseEntity<ResumoDiaDto> obterResumoDia() {
        ResumoDiaDto resumo = obterResumoDiaUseCase.obterResumoDia();
        return ResponseEntity.ok(resumo);
    }
}