package dev.amigoes.starlighttimer.adapters.identity.in.web;

import dev.amigoes.starlighttimer.app.identity.ports.in.UsuarioAtualPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.UsuarioNaoAutenticadoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HeaderUsuarioAtualAdapter implements UsuarioAtualPort {

    private final HttpServletRequest request;

    public HeaderUsuarioAtualAdapter(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public UUID obterUsuarioIdAtual() {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new UsuarioNaoAutenticadoException("Usuário não autenticado. Cabeçalho X-User-Id ausente.");
        }
        try {
            return UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException e) {
            throw new UsuarioNaoAutenticadoException("Identificador de usuário inválido.");
        }
    }
}