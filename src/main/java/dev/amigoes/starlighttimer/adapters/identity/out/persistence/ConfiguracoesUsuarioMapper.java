package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

import dev.amigoes.starlighttimer.domain.identity.entities.ConfiguracoesUsuario;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class ConfiguracoesUsuarioMapper {
    public ConfiguracoesUsuarioEntity paraEntity(ConfiguracoesUsuario configuracoes) {
        Objects.requireNonNull(configuracoes, "As configuracoes sao obrigatorias");
        return new ConfiguracoesUsuarioEntity(configuracoes.getUsuarioId(),
                configuracoes.getFocoEmSegundos(), configuracoes.getPausaEmSegundos(),
                configuracoes.isIniciarPausaAuto(), configuracoes.isSomHabilitado());
    }

    public ConfiguracoesUsuario paraDominio(ConfiguracoesUsuarioEntity entity) {
        Objects.requireNonNull(entity, "As configuracoes persistidas sao obrigatorias");
        if (entity.getFocoEmSegundos() == null || entity.getPausaEmSegundos() == null
                || entity.getIniciarPausaAuto() == null || entity.getSomHabilitado() == null) {
            throw new IllegalStateException("Configuracoes persistidas incompletas");
        }
        return new ConfiguracoesUsuario(entity.getUsuarioId(), entity.getFocoEmSegundos(),
                entity.getPausaEmSegundos(), entity.getIniciarPausaAuto(), entity.getSomHabilitado());
    }
}
