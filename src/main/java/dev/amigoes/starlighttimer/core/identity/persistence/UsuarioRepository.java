package dev.amigoes.starlighttimer.core.identity.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Objects;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, UUID>, CadastroUsuario {
    boolean existsByEmail(String email);

    boolean existsByIdAndStatusAndExcluidoEmIsNull(UUID id, String status);

    default boolean existePorEmail(String email) {
        return existsByEmail(Objects.requireNonNull(email, "O email e obrigatorio"));
    }

    default boolean existeAtivoPorId(UUID id) {
        return existsByIdAndStatusAndExcluidoEmIsNull(id, "ACTIVE");
    }
}
