package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.mapper.CatalogoMapper;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.InsigniaUsuarioId;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.InsigniaUsuarioJpaRepository;
import dev.amigoes.starlighttimer.app.progression.ports.out.RegistrarProgressoInsigniaPort;
import dev.amigoes.starlighttimer.domain.progression.entities.ProgressoInsignia;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class SpringRegistrarProgressoInsigniaAdapter implements RegistrarProgressoInsigniaPort {

    private final InsigniaUsuarioJpaRepository insigniaUsuarioJpaRepository;
    private final CatalogoMapper mapper;

    public SpringRegistrarProgressoInsigniaAdapter(
            InsigniaUsuarioJpaRepository insigniaUsuarioJpaRepository,
            CatalogoMapper mapper
    ) {
        this.insigniaUsuarioJpaRepository = insigniaUsuarioJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProgressoInsignia> buscarProgresso(UUID usuarioId, UUID insigniaId) {
        return insigniaUsuarioJpaRepository.findById(new InsigniaUsuarioId(usuarioId, insigniaId))
                .map(mapper::toDomain);
    }

    @Override
    public void registrarProgresso(ProgressoInsignia progresso) {
        insigniaUsuarioJpaRepository.salvarMelhorProgresso(progresso.getUsuarioId(),
                progresso.getInsigniaId(), progresso.getContagemProgresso(), progresso.getDesbloqueadoEm());
    }
}
