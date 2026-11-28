package dev.amigoes.starlighttimer.core.identity.currentuser;

import dev.amigoes.starlighttimer.core.identity.domain.UsuarioNaoAutenticadoException;
import dev.amigoes.starlighttimer.core.identity.persistence.UsuarioRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("!local")
public class JwtUsuarioAtualAdapter implements UsuarioAtualPort {

    private final UsuarioRepository usuarios;

    public JwtUsuarioAtualAdapter(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UUID getUsuarioId() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (!(autenticacao instanceof JwtAuthenticationToken jwt) || !jwt.isAuthenticated()) {
            throw new UsuarioNaoAutenticadoException("Token ausente, invalido ou expirado");
        }

        String sujeito = jwt.getToken().getSubject();
        if (sujeito == null) {
            throw new UsuarioNaoAutenticadoException("Token ausente, invalido ou expirado");
        }

        UUID id;
        try {
            id = UUID.fromString(sujeito);
        } catch (IllegalArgumentException ex) {
            throw new UsuarioNaoAutenticadoException("Token ausente, invalido ou expirado");
        }
        if (!usuarios.existeAtivoPorId(id)) {
            throw new UsuarioNaoAutenticadoException("Token ausente, invalido ou expirado");
        }
        return id;
    }
}
