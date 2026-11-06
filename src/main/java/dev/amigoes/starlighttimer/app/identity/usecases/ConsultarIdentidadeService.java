package dev.amigoes.starlighttimer.app.identity.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.ConsultarIdentidadePort;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultarIdentidadeService implements ConsultarIdentidadePort {
    private final UsuarioAtualPort usuarioAtual;
    private final UsuarioRepositoryPort usuarios;

    public ConsultarIdentidadeService(UsuarioAtualPort usuarioAtual, UsuarioRepositoryPort usuarios) {
        this.usuarioAtual = usuarioAtual;
        this.usuarios = usuarios;
    }

    @Override
    public UUID obterUsuarioAtual() {
        return usuarioAtual.getUsuarioId();
    }

    @Override
    public boolean existeUsuario(UUID id) {
        return usuarios.existePorId(id);
    }
}
