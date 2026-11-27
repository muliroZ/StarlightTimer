package dev.amigoes.starlighttimer.core.progression.persistence;

import dev.amigoes.starlighttimer.records.progression.Constelacao;
import dev.amigoes.starlighttimer.records.progression.Insignia;
import dev.amigoes.starlighttimer.records.progression.LimiteNivel;
import dev.amigoes.starlighttimer.records.progression.Patente;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CatalogoProgressaoRepository {

    private final LimiteNivelRepository limiteNivelRepository;
    private final PatenteRepository patenteRepository;
    private final InsigniaRepository insigniaRepository;
    private final ConstelacaoRepository constelacaoRepository;
    private final InsigniaConstelacaoRepository insigniaConstelacaoRepository;
    private final CatalogoMapper mapper;

    public CatalogoProgressaoRepository(
            LimiteNivelRepository limiteNivelRepository,
            PatenteRepository patenteRepository,
            InsigniaRepository insigniaRepository,
            ConstelacaoRepository constelacaoRepository,
            InsigniaConstelacaoRepository insigniaConstelacaoRepository,
            CatalogoMapper mapper
    ) {
        this.limiteNivelRepository = limiteNivelRepository;
        this.patenteRepository = patenteRepository;
        this.insigniaRepository = insigniaRepository;
        this.constelacaoRepository = constelacaoRepository;
        this.insigniaConstelacaoRepository = insigniaConstelacaoRepository;
        this.mapper = mapper;
    }

    public List<LimiteNivel> buscarLimites() {
        return limiteNivelRepository.findAll(Sort.by(Sort.Direction.ASC, "nivel"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Patente> buscarPatentes() {
        return patenteRepository.findAll(Sort.by(Sort.Direction.ASC, "nivelMinimo"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Insignia> buscarInsignias() {
        return insigniaRepository.findAll(Sort.by(Sort.Direction.ASC, "tipoCriterio"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Constelacao> buscarConstelacoes() {
        return constelacaoRepository.findAll(Sort.by("codigo")).stream()
                .map(entity -> mapper.toDomain(entity,
                        insigniaConstelacaoRepository.findByIdConstelacaoId(entity.getId())))
                .toList();
    }
}
