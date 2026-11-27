package dev.amigoes.starlighttimer.controller;

import dev.amigoes.starlighttimer.core.identity.currentuser.UsuarioAtualPort;
import dev.amigoes.starlighttimer.core.progression.usecase.ConsultarProgressaoService;
import dev.amigoes.starlighttimer.records.progression.ProgressaoResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/progressao")
public class ProgressaoController {
    private final ConsultarProgressaoService consulta;
    private final UsuarioAtualPort usuarioAtual;

    public ProgressaoController(ConsultarProgressaoService consulta, UsuarioAtualPort usuarioAtual) {
        this.consulta = consulta;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    public ProgressaoResponse consultar() {
        return ProgressaoResponse.de(consulta.consultar(usuarioAtual.getUsuarioId()));
    }
}
