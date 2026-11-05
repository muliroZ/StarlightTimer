package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.mapper.CatalogoMapper;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.LimiteNivelJpaRepository;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.PatenteJpaRepository;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerCatalogoProgressaoPort;
import dev.amigoes.starlighttimer.domain.progression.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.progression.entities.Patente;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SpringLerCatalogoProgressaoAdapter implements LerCatalogoProgressaoPort {

    private final LimiteNivelJpaRepository limiteNivelJpaRepository;
    private final PatenteJpaRepository patenteJpaRepository;
    private final CatalogoMapper mapper;

    public SpringLerCatalogoProgressaoAdapter(
            LimiteNivelJpaRepository limiteNivelJpaRepository,
            PatenteJpaRepository patenteJpaRepository,
            CatalogoMapper mapper
    ) {
        this.limiteNivelJpaRepository = limiteNivelJpaRepository;
        this.patenteJpaRepository = patenteJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<LimiteNivel> buscarLimites() {
        return limiteNivelJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "nivel"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Patente> buscarPatentes() {
        return patenteJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "nivelMinimo"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
