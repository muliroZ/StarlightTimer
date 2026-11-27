package dev.amigoes.starlighttimer.records.social;

import java.util.List;

public record PedidosPendentesResponse(List<PedidoAmizadeResponse> recebidos,
                                       List<PedidoAmizadeResponse> enviados) {
    public static PedidosPendentesResponse de(PedidosPendentes pedidos) {
        return new PedidosPendentesResponse(
                pedidos.recebidos().stream().map(PedidoAmizadeResponse::de).toList(),
                pedidos.enviados().stream().map(PedidoAmizadeResponse::de).toList());
    }
}
