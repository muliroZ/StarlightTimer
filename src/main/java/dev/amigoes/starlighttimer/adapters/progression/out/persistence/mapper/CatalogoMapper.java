package dev.amigoes.starlighttimer.adapters.progression.out.persistence.mapper;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.model.*;
import dev.amigoes.starlighttimer.domain.progression.entities.Insignia;
import dev.amigoes.starlighttimer.domain.progression.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.progression.entities.Patente;
import dev.amigoes.starlighttimer.domain.progression.entities.ProgressoInsignia;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CatalogoMapper {

    public LimiteNivel toDomain(LimiteNivelEntity entity) {
        return new LimiteNivel(entity.getNivel(), entity.getXpNecessario());
    }

    public Patente toDomain(PatenteEntity entity) {
        return new Patente(
                entity.getId(),
                entity.getCodigo(),
                entity.getNome(),
                entity.getNivelMinimo().getNivel(),
                entity.getImagemTitulo()
        );
    }

    public Insignia toDomain(InsigniaEntity entity) {
        return new Insignia(
                entity.getId(),
                entity.getCodigo(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getIcone(),
                entity.getXpRecompensa(),
                entity.getCriterioJson(),
                entity.getTipoCriterio(),
                entity.getContagemAlvo()
        );
    }

    public InsigniaUsuarioEntity toEntity(ProgressoInsignia progresso) {
        return new InsigniaUsuarioEntity(
                new InsigniaUsuarioId(progresso.getUsuarioId(), progresso.getInsigniaId()),
                progresso.getContagemProgresso(),
                progresso.getDesbloqueadoEm()
        );
    }

    public ProgressoInsignia toDomain(InsigniaUsuarioEntity entity) {
        return new ProgressoInsignia(entity.getId().getUsuarioId(), entity.getId().getInsigniaId(),
                entity.getContagemProgresso(), entity.getDesbloqueadoEm());
    }

    public dev.amigoes.starlighttimer.domain.progression.entities.Constelacao toDomain(
            ConstelacaoEntity entity, List<InsigniaConstelacaoEntity> vinculos) {
        return new dev.amigoes.starlighttimer.domain.progression.entities.Constelacao(
                entity.getId(), entity.getCodigo(), entity.getNome(), entity.getXpRecompensa(),
                entity.getQtdEstrelas(), entity.getQtdInsigniasRequeridas(), entity.getDescricao(),
                entity.getAssetSvg(), vinculos.stream().map(v -> v.getId().getInsigniaId())
                        .collect(Collectors.toSet()));
    }
}
