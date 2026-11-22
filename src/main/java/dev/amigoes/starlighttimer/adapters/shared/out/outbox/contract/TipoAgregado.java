package dev.amigoes.starlighttimer.adapters.shared.out.outbox.contract;

import java.util.Map;

public enum TipoAgregado {
    AMIZADE,
    PEDIDO_AMIZADE,
    PERFIL,
    SESSAO,
    USUARIO;

    public String definirTopico() {
        String prefixo = "starlight.";

        Map<TipoAgregado, String> topicos = Map.of(
                AMIZADE,        "amizades",
                PEDIDO_AMIZADE, "pedidos-amizade",
                PERFIL,         "perfis",
                SESSAO,         "sessoes",
                USUARIO,        "usuarios"
        );

        return prefixo + topicos.get(this);
    }
}
