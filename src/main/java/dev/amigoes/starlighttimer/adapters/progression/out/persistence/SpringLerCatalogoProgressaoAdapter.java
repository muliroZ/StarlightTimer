package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.mapper.CatalogoMapper;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.InsigniaJpaRepository;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.LimiteNivelJpaRepository;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.PatenteJpaRepository;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.ConstelacaoJpaRepository;
import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.InsigniaConstelacaoJpaRepository;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerCatalogoProgressaoPort;
import dev.amigoes.starlighttimer.domain.progression.entities.Insignia;
import dev.amigoes.starlighttimer.domain.progression.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.progression.entities.Patente;
import dev.amigoes.starlighttimer.domain.progression.entities.Constelacao;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpringLerCatalogoProgressaoAdapter implements LerCatalogoProgressaoPort {

    private final LimiteNivelJpaRepository limiteNivelJpaRepository;
    private final PatenteJpaRepository patenteJpaRepository;
    private final InsigniaJpaRepository insigniaJpaRepository;
    private final ConstelacaoJpaRepository constelacaoJpaRepository;
    private final InsigniaConstelacaoJpaRepository insigniaConstelacaoJpaRepository;
    private final CatalogoMapper mapper;

    public SpringLerCatalogoProgressaoAdapter(
            LimiteNivelJpaRepository limiteNivelJpaRepository,
            PatenteJpaRepository patenteJpaRepository,
            InsigniaJpaRepository insigniaJpaRepository,
            ConstelacaoJpaRepository constelacaoJpaRepository,
            InsigniaConstelacaoJpaRepository insigniaConstelacaoJpaRepository,
            CatalogoMapper mapper
    ) {
        this.limiteNivelJpaRepository = limiteNivelJpaRepository;
        this.patenteJpaRepository = patenteJpaRepository;
        this.insigniaJpaRepository = insigniaJpaRepository;
        this.constelacaoJpaRepository = constelacaoJpaRepository;
        this.insigniaConstelacaoJpaRepository = insigniaConstelacaoJpaRepository;
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

    @Override
    public List<Insignia> buscarInsignias() {
        return insigniaJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "tipoCriterio"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Constelacao> buscarConstelacoes() {
        return constelacaoJpaRepository.findAll(Sort.by("codigo")).stream()
                .map(entity -> mapper.toDomain(entity,
                        insigniaConstelacaoJpaRepository.findByIdConstelacaoId(entity.getId())))
                .toList();
    }
}
