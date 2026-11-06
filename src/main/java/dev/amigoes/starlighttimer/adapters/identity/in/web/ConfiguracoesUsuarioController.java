package dev.amigoes.starlighttimer.adapters.identity.in.web;

import dev.amigoes.starlighttimer.app.identity.ports.in.AtualizarConfiguracoesUsuarioUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.ObterConfiguracoesUsuarioUseCase;
import dev.amigoes.starlighttimer.domain.identity.entities.ConfiguracoesUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/configuracoes")
public class ConfiguracoesUsuarioController {

    private final ObterConfiguracoesUsuarioUseCase obterConfiguracoesUsuarioUseCase;
    private final AtualizarConfiguracoesUsuarioUseCase atualizarConfiguracoesUsuarioUseCase;

    public ConfiguracoesUsuarioController(ObterConfiguracoesUsuarioUseCase obterConfiguracoesUsuarioUseCase,
                                          AtualizarConfiguracoesUsuarioUseCase atualizarConfiguracoesUsuarioUseCase) {
        this.obterConfiguracoesUsuarioUseCase = obterConfiguracoesUsuarioUseCase;
        this.atualizarConfiguracoesUsuarioUseCase = atualizarConfiguracoesUsuarioUseCase;
    }

    @GetMapping
    public ResponseEntity<ConfiguracoesUsuarioResponse> obterConfiguracoes() {
        ConfiguracoesUsuario config = obterConfiguracoesUsuarioUseCase.obterConfiguracoes();
        return ResponseEntity.ok(ConfiguracoesUsuarioResponse.deDominio(config));
    }

    @PatchMapping
    public ResponseEntity<ConfiguracoesUsuarioResponse> atualizarConfiguracoes(@RequestBody AtualizarConfiguracoesUsuarioRequest requisicao) {
        ConfiguracoesUsuario configAtualizada = atualizarConfiguracoesUsuarioUseCase.atualizarConfiguracoes(
                requisicao.focoEmSegundos(),
                requisicao.pausaEmSegundos(),
                requisicao.iniciarPausaAuto(),
                requisicao.somHabilitado()
        );
        return ResponseEntity.ok(ConfiguracoesUsuarioResponse.deDominio(configAtualizada));
    }
}