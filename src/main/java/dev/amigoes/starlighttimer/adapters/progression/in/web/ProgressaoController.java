package dev.amigoes.starlighttimer.adapters.progression.in.web;

import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.progression.ports.in.ConsultarProgressaoUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/progressao")
public class ProgressaoController {
    private final ConsultarProgressaoUseCase consulta;
    private final UsuarioAtualPort usuarioAtual;

    public ProgressaoController(ConsultarProgressaoUseCase consulta, UsuarioAtualPort usuarioAtual) {
        this.consulta = consulta;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    public ProgressaoResponse consultar() {
        return ProgressaoResponse.de(consulta.consultar(usuarioAtual.getUsuarioId()));
    }
}
