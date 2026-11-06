package dev.amigoes.starlighttimer.adapters.identity.out.web;

import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.domain.identity.exceptions.UsuarioNaoAutenticadoException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("!local")
public class HeaderUsuarioAtualAdapter implements UsuarioAtualPort {

    @Override
    public UUID getUsuarioId() {
        throw new UsuarioNaoAutenticadoException("Autenticação JWT ainda não disponível.");
    }
}
