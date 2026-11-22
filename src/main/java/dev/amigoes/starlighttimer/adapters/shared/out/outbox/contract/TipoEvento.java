package dev.amigoes.starlighttimer.adapters.shared.out.outbox.contract;

import java.util.Map;

public enum TipoEvento {
    CONSTELACAO_DESBLOQUEADA,
    INSIGNIA_DESBLOQUEADA,
    PEDIDO_AMIZADE_ACEITO,
    PEDIDO_AMIZADE_ENVIADO,
    SESSAO_ABANDONADA,
    SESSAO_CONCLUIDA,
    SESSAO_INICIADA;

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
