package dev.amigoes.starlighttimer.events.kafka;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public enum TipoEvento {
    CONSTELACAO_DESBLOQUEADA("constelacao-desbloqueada"),
    INSIGNIA_DESBLOQUEADA("insignia-desbloqueada"),
    PEDIDO_AMIZADE_ACEITO("pedido-amizade-aceito"),
    PEDIDO_AMIZADE_ENVIADO("pedido-amizade-enviado"),
    SESSAO_ABANDONADA("sessao-abandonada"),
    SESSAO_CONCLUIDA("sessao-concluida"),
    SESSAO_INICIADA("sessao-iniciada");

    private final String valor;

    TipoEvento(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String valor() {
        return valor;
    }

    public TipoAgregado derivarAgregado() {
        Map<TipoEvento, TipoAgregado> derivacoes = Map.of(
                CONSTELACAO_DESBLOQUEADA,   TipoAgregado.PERFIL,
                INSIGNIA_DESBLOQUEADA,      TipoAgregado.PERFIL,
                PEDIDO_AMIZADE_ACEITO,      TipoAgregado.PEDIDO_AMIZADE,
                PEDIDO_AMIZADE_ENVIADO,     TipoAgregado.PEDIDO_AMIZADE,
                SESSAO_ABANDONADA,          TipoAgregado.SESSAO,
                SESSAO_CONCLUIDA,           TipoAgregado.SESSAO,
                SESSAO_INICIADA,            TipoAgregado.SESSAO
        );

        return derivacoes.get(this);
    }
}
