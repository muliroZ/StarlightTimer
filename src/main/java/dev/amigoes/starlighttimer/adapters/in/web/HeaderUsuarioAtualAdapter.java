package dev.amigoes.starlighttimer.adapters.in.web;

import dev.amigoes.starlighttimer.app.ports.in.UsuarioAtualPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.UsuarioNaoAutenticadoException;import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
public class HeaderUsuarioAtualAdapter implements UsuarioAtualPort {

    public static final String HEADER_USUARIO_ID = "X-Usuario-Id";
    private final HttpServletRequest request;

    public HeaderUsuarioAtualAdapter(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public UUID getUsuarioId() {
        String headerValue = request.getHeader(HEADER_USUARIO_ID);

        if (headerValue == null || headerValue.isBlank()) {
            throw new UsuarioNaoAutenticadoException("Cabeçalho " + HEADER_USUARIO_ID + " ausente.");
        }

        try {
            return UUID.fromString(headerValue.trim());
        } catch (IllegalArgumentException e) {
            throw new UsuarioNaoAutenticadoException("Cabeçalho " + HEADER_USUARIO_ID + " contém um UUID inválido.");
        }
    }
}