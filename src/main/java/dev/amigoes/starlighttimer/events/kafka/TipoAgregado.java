package dev.amigoes.starlighttimer.events.kafka;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public enum TipoAgregado {
    AMIZADE("amizade"),
    PEDIDO_AMIZADE("pedido-amizade"),
    PERFIL("perfil"),
    SESSAO("sessao"),
    USUARIO("usuario");

    private final String valor;

    TipoAgregado(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String valor() {
        return valor;
    }

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
