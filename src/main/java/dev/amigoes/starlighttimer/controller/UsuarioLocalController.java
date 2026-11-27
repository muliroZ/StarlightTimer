package dev.amigoes.starlighttimer.controller;

import dev.amigoes.starlighttimer.core.identity.usecase.CadastrarUsuarioService;
import dev.amigoes.starlighttimer.records.identity.CadastroUsuarioComando;
import dev.amigoes.starlighttimer.records.identity.CriarUsuarioLocalRequest;
import dev.amigoes.starlighttimer.records.identity.UsuarioLocalResponse;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("local")
@RequestMapping("/api/v1/usuarios/local")
public class UsuarioLocalController {
    private final CadastrarUsuarioService cadastro;

    public UsuarioLocalController(CadastrarUsuarioService cadastro) {
        this.cadastro = cadastro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioLocalResponse criar(@Valid @RequestBody CriarUsuarioLocalRequest request) {
        var usuario = cadastro.cadastrar(new CadastroUsuarioComando(
                request.email(), request.nomeExibido(), request.fusoHorario()));
        return new UsuarioLocalResponse(usuario.getId(), usuario.getEmail(),
                usuario.getPerfil().getNomeExibido(), usuario.getPerfil().getFusoHorario());
    }


}
