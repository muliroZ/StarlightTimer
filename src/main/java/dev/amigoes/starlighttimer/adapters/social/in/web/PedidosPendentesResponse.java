package dev.amigoes.starlighttimer.adapters.social.in.web;

import dev.amigoes.starlighttimer.app.social.ports.in.GerenciarCirculoSocialUseCase;

import java.util.List;

public record PedidosPendentesResponse(List<PedidoAmizadeResponse> recebidos,
                                       List<PedidoAmizadeResponse> enviados) {
    public static PedidosPendentesResponse de(GerenciarCirculoSocialUseCase.PedidosPendentes pedidos) {
        return new PedidosPendentesResponse(
                pedidos.recebidos().stream().map(PedidoAmizadeResponse::de).toList(),
                pedidos.enviados().stream().map(PedidoAmizadeResponse::de).toList());
    }
}
