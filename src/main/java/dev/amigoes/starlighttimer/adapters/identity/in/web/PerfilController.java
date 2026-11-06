package dev.amigoes.starlighttimer.adapters.identity.in.web;

import dev.amigoes.starlighttimer.app.identity.ports.in.AtualizarPerfilUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.ObterPerfilUseCase;
import dev.amigoes.starlighttimer.domain.social.entities.Perfil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/perfil")
public class PerfilController {

    private final ObterPerfilUseCase obterPerfilUseCase;
    private final AtualizarPerfilUseCase atualizarPerfilUseCase;

    public PerfilController(ObterPerfilUseCase obterPerfilUseCase, AtualizarPerfilUseCase atualizarPerfilUseCase) {
        this.obterPerfilUseCase = obterPerfilUseCase;
        this.atualizarPerfilUseCase = atualizarPerfilUseCase;
    }

    @GetMapping
    public ResponseEntity<PerfilResponse> obterPerfil() {
        Perfil perfil = obterPerfilUseCase.obterPerfil();
        return ResponseEntity.ok(PerfilResponse.deDominio(perfil));
    }

    @PatchMapping
    public ResponseEntity<PerfilResponse> atualizarPerfil(@RequestBody AtualizarPerfilRequest requisicao) {
        Perfil perfilAtualizado = atualizarPerfilUseCase.atualizarPerfil(
                requisicao.nomeExibido(),
                requisicao.avatar(),
                requisicao.bio(),
                requisicao.fusoHorario()
        );
        return ResponseEntity.ok(PerfilResponse.deDominio(perfilAtualizado));
    }
}