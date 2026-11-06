package dev.amigoes.starlighttimer.app.social.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.ConsultarIdentidadePort;
import dev.amigoes.starlighttimer.app.social.ports.in.AcaoPedidoAmizade;
import dev.amigoes.starlighttimer.app.social.ports.in.GerenciarPedidosAmizadePort;
import dev.amigoes.starlighttimer.app.social.ports.in.ResultadoPedidoAmizade;
import dev.amigoes.starlighttimer.app.social.ports.out.AmizadeRepositoryPort;
import dev.amigoes.starlighttimer.app.social.ports.out.BloqueioRepositoryPort;
import dev.amigoes.starlighttimer.app.social.ports.out.PedidoAmizadeRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.ConflitoException;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.domain.social.entities.PedidoAmizade;
import dev.amigoes.starlighttimer.domain.social.exceptions.RegraAmizadeVioladaExcecao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class PedidoAmizadeService implements GerenciarPedidosAmizadePort {
    private final ConsultarIdentidadePort identidade;
    private final PedidoAmizadeRepositoryPort pedidos;
    private final AmizadeRepositoryPort amizades;
    private final BloqueioRepositoryPort bloqueios;
    private final Clock relogio;

    public PedidoAmizadeService(ConsultarIdentidadePort identidade, PedidoAmizadeRepositoryPort pedidos,
                               AmizadeRepositoryPort amizades, BloqueioRepositoryPort bloqueios, Clock relogio) {
        this.identidade = identidade;
        this.pedidos = pedidos;
        this.amizades = amizades;
        this.bloqueios = bloqueios;
        this.relogio = relogio;
    }

    @Override
    @Transactional
    public PedidoAmizade enviar(UUID destinatarioId) {
        UUID remetenteId = identidade.obterUsuarioAtual();
        PedidoAmizade pedido = PedidoAmizade.criarNovo(UUID.randomUUID(), remetenteId,
                destinatarioId, relogio.instant());
        if (!identidade.existeUsuarioAtivo(remetenteId)) {
            throw new RecursoNaoEncontradoException("Usuario atual nao encontrado");
        }
        if (!identidade.existeUsuarioAtivo(destinatarioId)) {
            throw new RecursoNaoEncontradoException("Destinatario nao encontrado");
        }
        pedidos.bloquearPar(remetenteId, destinatarioId);
        validarParDisponivel(remetenteId, destinatarioId);
        pedidos.criar(pedido);
        return pedido;
    }

    @Override
    @Transactional
    public ResultadoPedidoAmizade responder(UUID pedidoId, AcaoPedidoAmizade acao) {
        UUID solicitanteId = identidade.obterUsuarioAtual();
        PedidoAmizade pedido = pedidos.buscarPorId(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido de amizade nao encontrado"));
        var agora = relogio.instant();
        switch (acao) {
            case ACEITAR -> pedido.aceitar(solicitanteId, agora);
            case RECUSAR -> pedido.recusar(solicitanteId, agora);
            case CANCELAR -> pedido.cancelar(solicitanteId, agora);
        }

        UUID remetente = pedido.getRemetenteId();
        UUID destinatario = pedido.getDestinatarioId();
        pedidos.bloquearPar(remetente, destinatario);
        if (acao == AcaoPedidoAmizade.ACEITAR) {
            validarParDisponivel(remetente, destinatario);
        }
        if (!pedidos.atualizarPendente(pedido)) {
            throw new ConflitoException("O pedido deixou de estar pendente");
        }

        if (acao == AcaoPedidoAmizade.ACEITAR) {
            var amizade = amizades.buscarEntre(remetente, destinatario)
                    .orElseThrow(() -> new IllegalStateException("O trigger da V5 nao criou a amizade"));
            return new ResultadoPedidoAmizade.Aceito(amizade);
        }
        return new ResultadoPedidoAmizade.Respondido(pedido);
    }

    private void validarParDisponivel(UUID primeiro, UUID segundo) {
        if (!bloqueios.buscarEntre(primeiro, segundo).isEmpty()) {
            throw new RegraAmizadeVioladaExcecao("Acao de amizade indisponivel para este par");
        }
        if (amizades.buscarEntre(primeiro, segundo).isPresent()) {
            throw new ConflitoException("A amizade ja existe");
        }
    }
}
