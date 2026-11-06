package dev.amigoes.starlighttimer.app.identity.ports.out;

import dev.amigoes.starlighttimer.domain.identity.entities.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {

    boolean existePorEmail(String email);

    Optional<Usuario> buscarPorId(UUID id);

    void criar(Usuario usuario);

    boolean existeAtivoPorId(UUID id);
}
