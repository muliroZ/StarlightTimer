package dev.amigoes.starlighttimer.controller;

import dev.amigoes.starlighttimer.core.identity.domain.ConfiguracoesUsuario;
import dev.amigoes.starlighttimer.core.identity.usecase.ConfiguracoesUsuarioService;
import dev.amigoes.starlighttimer.records.identity.AtualizarConfiguracoesUsuarioRequest;
import dev.amigoes.starlighttimer.records.identity.ConfiguracoesUsuarioResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/configuracoes")
public class ConfiguracoesUsuarioController {

    private final ConfiguracoesUsuarioService configuracoes;

    public ConfiguracoesUsuarioController(ConfiguracoesUsuarioService configuracoes) {
        this.configuracoes = configuracoes;
    }

    @GetMapping
    public ResponseEntity<ConfiguracoesUsuarioResponse> obterConfiguracoes() {
        ConfiguracoesUsuario config = configuracoes.obterConfiguracoes();
        return ResponseEntity.ok(ConfiguracoesUsuarioResponse.deDominio(config));
    }

    @PatchMapping
    public ResponseEntity<ConfiguracoesUsuarioResponse> atualizarConfiguracoes(@RequestBody AtualizarConfiguracoesUsuarioRequest requisicao) {
        ConfiguracoesUsuario configAtualizada = configuracoes.atualizarConfiguracoes(
                requisicao.focoEmSegundos(),
                requisicao.pausaEmSegundos(),
                requisicao.iniciarPausaAuto(),
                requisicao.somHabilitado()
        );
        return ResponseEntity.ok(ConfiguracoesUsuarioResponse.deDominio(configAtualizada));
    }
}
