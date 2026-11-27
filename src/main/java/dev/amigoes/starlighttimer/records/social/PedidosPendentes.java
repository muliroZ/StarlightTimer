package dev.amigoes.starlighttimer.records.social;

import dev.amigoes.starlighttimer.core.social.domain.PedidoAmizade;

import java.util.List;

public record PedidosPendentes(List<PedidoAmizade> recebidos, List<PedidoAmizade> enviados) {
}
