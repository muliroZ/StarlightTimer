package dev.amigoes.starlighttimer.adapters.identity.out.web;

import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioRepositoryPort;
import dev.amigoes.starlighttimer.config.UsuarioLocalConfig;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
@Profile("local")
public class UsuarioLocalAtualAdapter implements UsuarioAtualPort {
    private final UsuarioRepositoryPort usuarios;

    public UsuarioLocalAtualAdapter(UsuarioRepositoryPort usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UUID getUsuarioId() {
        UUID id = UsuarioLocalConfig.USUARIO_ID;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos) {
            String cabecalho = atributos.getRequest().getHeader("X-Usuario-Id");
            if (cabecalho != null && !cabecalho.isBlank()) {
                id = UUID.fromString(cabecalho);
            }
        }
        if (!usuarios.existeAtivoPorId(id)) {
            throw new RecursoNaoEncontradoException("Usuário local não encontrado");
        }
        return id;
    }
}
