package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.ConstelacaoUsuarioJpaRepository;
import dev.amigoes.starlighttimer.app.progression.ports.out.RegistrarConstelacaoUsuarioPort;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Component
public class SpringRegistrarConstelacaoUsuarioAdapter implements RegistrarConstelacaoUsuarioPort {
    private final ConstelacaoUsuarioJpaRepository repositorio;
    public SpringRegistrarConstelacaoUsuarioAdapter(ConstelacaoUsuarioJpaRepository repositorio) {
        this.repositorio = repositorio;
    }
    @Override
    public boolean registrarDesbloqueio(UUID constelacaoId, UUID usuarioId, Instant desbloqueadoEm) {
        return repositorio.inserirDesbloqueio(constelacaoId, usuarioId, desbloqueadoEm) == 1;
    }
}
