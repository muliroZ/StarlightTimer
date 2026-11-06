package dev.amigoes.starlighttimer.app.social.ports.in;

import dev.amigoes.starlighttimer.app.identity.ports.in.ResumoPerfilPublico;
import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;

import java.util.List;
import java.util.UUID;

public interface GerenciarCirculoSocialUseCase {
    List<ResumoPerfilPublico> listarAmigos();
    PedidosPendentes listarPedidos();
    void desfazerAmizade(UUID amizadeId);
    void bloquear(UUID usuarioId);
    void desbloquear(UUID usuarioId);

    record PedidosPendentes(List<PedidoAmizade> recebidos, List<PedidoAmizade> enviados) {
    }
}
