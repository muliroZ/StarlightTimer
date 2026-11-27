package dev.amigoes.starlighttimer.core.identity.currentuser;

import dev.amigoes.starlighttimer.core.identity.domain.UsuarioNaoAutenticadoException;

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
