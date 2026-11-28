package dev.amigoes.starlighttimer.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

@Component
public class ProblemDetailAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper json;

    public ProblemDetailAuthenticationEntryPoint(ObjectMapper json) {
        this.json = json;
    }

    @Override
    public void commence(HttpServletRequest requisicao, HttpServletResponse resposta,
                         AuthenticationException erro) throws IOException {
        var problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Token ausente, invalido ou expirado");
        problema.setTitle("Nao autenticado");
        problema.setInstance(URI.create(requisicao.getRequestURI()));

        resposta.setStatus(problema.getStatus());
        resposta.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        resposta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        json.writeValue(resposta.getOutputStream(), problema);
    }
}
