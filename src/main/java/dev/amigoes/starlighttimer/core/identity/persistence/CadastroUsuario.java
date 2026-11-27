package dev.amigoes.starlighttimer.core.identity.persistence;

import dev.amigoes.starlighttimer.core.identity.domain.Usuario;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

interface CadastroUsuario {
    @Transactional(readOnly = true)
    Optional<Usuario> buscarPorId(UUID id);

    @Transactional(propagation = Propagation.MANDATORY)
    void criar(Usuario usuario);
}
