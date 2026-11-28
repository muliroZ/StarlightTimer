package dev.amigoes.starlighttimer.controller;

import dev.amigoes.starlighttimer.core.identity.usecase.AutenticacaoService;
import dev.amigoes.starlighttimer.core.identity.usecase.CadastrarContaService;
import dev.amigoes.starlighttimer.records.identity.CadastroContaRequest;
import dev.amigoes.starlighttimer.records.identity.ContaCriadaResponse;
import dev.amigoes.starlighttimer.records.identity.LoginRequest;
import dev.amigoes.starlighttimer.records.identity.RefreshTokenRequest;
import dev.amigoes.starlighttimer.records.identity.TokensEmitidos;
import dev.amigoes.starlighttimer.records.identity.TokensResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {

    private final CadastrarContaService cadastro;
    private final AutenticacaoService autenticacao;

    public AutenticacaoController(CadastrarContaService cadastro, AutenticacaoService autenticacao) {
        this.cadastro = cadastro;
        this.autenticacao = autenticacao;
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public ContaCriadaResponse cadastrar(@Valid @RequestBody CadastroContaRequest request) {
        var usuario = cadastro.cadastrar(request);
        return new ContaCriadaResponse(usuario.getId(), usuario.getEmail(),
                usuario.getPerfil().getNomeExibido(), usuario.getPerfil().getFusoHorario());
    }

    @PostMapping("/login")
    public TokensResponse login(@Valid @RequestBody LoginRequest request,
                                @RequestHeader(name = HttpHeaders.USER_AGENT, required = false) String userAgent) {
        return resposta(autenticacao.login(request, userAgent));
    }

    @PostMapping("/refresh")
    public TokensResponse renovar(@Valid @RequestBody RefreshTokenRequest request,
                                  @RequestHeader(name = HttpHeaders.USER_AGENT, required = false) String userAgent) {
        return resposta(autenticacao.renovar(request.refreshToken(), userAgent));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sair(@Valid @RequestBody RefreshTokenRequest request) {
        autenticacao.sair(request.refreshToken());
    }

    private TokensResponse resposta(TokensEmitidos tokens) {
        return new TokensResponse(tokens.accessToken(), tokens.refreshToken(), "Bearer", tokens.expiraEmSegundos());
    }
}
