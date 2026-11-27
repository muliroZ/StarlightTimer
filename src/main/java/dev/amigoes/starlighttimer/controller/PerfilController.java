package dev.amigoes.starlighttimer.controller;

import dev.amigoes.starlighttimer.core.identity.domain.Perfil;
import dev.amigoes.starlighttimer.core.identity.usecase.PerfilService;
import dev.amigoes.starlighttimer.records.identity.AtualizarPerfilRequest;
import dev.amigoes.starlighttimer.records.identity.PerfilResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/perfil")
public class PerfilController {

    private final PerfilService perfis;

    public PerfilController(PerfilService perfis) {
        this.perfis = perfis;
    }

    @GetMapping
    public ResponseEntity<PerfilResponse> obterPerfil() {
        Perfil perfil = perfis.obterPerfil();
        return ResponseEntity.ok(PerfilResponse.deDominio(perfil));
    }

    @PatchMapping
    public ResponseEntity<PerfilResponse> atualizarPerfil(@RequestBody AtualizarPerfilRequest requisicao) {
        Perfil perfilAtualizado = perfis.atualizarPerfil(
                requisicao.nomeExibido(),
                requisicao.avatar(),
                requisicao.bio(),
                requisicao.fusoHorario()
        );
        return ResponseEntity.ok(PerfilResponse.deDominio(perfilAtualizado));
    }
}
