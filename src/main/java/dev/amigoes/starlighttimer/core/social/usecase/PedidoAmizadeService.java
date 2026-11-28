package dev.amigoes.starlighttimer.core.social.usecase;

import dev.amigoes.starlighttimer.core.identity.usecase.ConsultarIdentidadeService;
import dev.amigoes.starlighttimer.core.interfaces.GravadorEventos;
import dev.amigoes.starlighttimer.core.social.domain.PedidoAmizade;
import dev.amigoes.starlighttimer.core.social.domain.RegraAmizadeVioladaExcecao;
import dev.amigoes.starlighttimer.core.social.persistence.AmizadeRepository;
import dev.amigoes.starlighttimer.core.social.persistence.BloqueioRepository;
import dev.amigoes.starlighttimer.core.social.persistence.PedidoAmizadeRepository;
import dev.amigoes.starlighttimer.enums.social.AcaoPedidoAmizade;
import dev.amigoes.starlighttimer.events.kafka.EventoEnvelope;
import dev.amigoes.starlighttimer.events.kafka.EventoMetadata;
import dev.amigoes.starlighttimer.events.kafka.TipoEvento;
import dev.amigoes.starlighttimer.events.kafka.payloads.PedidoAmizadeAceitoPayloadV1;
import dev.amigoes.starlighttimer.events.kafka.payloads.PedidoAmizadeEnviadoPayloadV1;
import dev.amigoes.starlighttimer.exceptions.ConflitoException;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.records.social.ResultadoPedidoAmizade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class PedidoAmizadeService {
    private final ConsultarIdentidadeService identidade;
    private final PedidoAmizadeRepository pedidos;
    private final AmizadeRepository amizades;
    private final BloqueioRepository bloqueios;
    private final GravadorEventos gravador;
    private final Clock relogio;

    public PedidoAmizadeService(ConsultarIdentidadeService identidade, PedidoAmizadeRepository pedidos,
                               AmizadeRepository amizades, BloqueioRepository bloqueios,
                               GravadorEventos gravador, Clock relogio) {
        this.identidade = identidade;
        this.pedidos = pedidos;
        this.amizades = amizades;
        this.bloqueios = bloqueios;
        this.gravador = gravador;
        this.relogio = relogio;
    }

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
        gravador.gravar(new EventoEnvelope<>(
                EventoMetadata.gerar(TipoEvento.PEDIDO_AMIZADE_ENVIADO, pedido.getId(), relogio, 1),
                new PedidoAmizadeEnviadoPayloadV1(remetenteId, destinatarioId)
        ));
        return pedido;
    }

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
            gravador.gravar(new EventoEnvelope<>(
                    EventoMetadata.gerar(TipoEvento.PEDIDO_AMIZADE_ACEITO, pedidoId, relogio, 1),
                    new PedidoAmizadeAceitoPayloadV1(remetente, destinatario, amizade.id())
            ));
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
