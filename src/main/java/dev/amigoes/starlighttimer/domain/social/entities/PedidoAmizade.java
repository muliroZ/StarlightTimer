package dev.amigoes.starlighttimer.domain.social.entities;

import dev.amigoes.starlighttimer.domain.social.enums.StatusPedidoAmizade;
import dev.amigoes.starlighttimer.domain.shared.exceptions.ConflitoException;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.domain.social.exceptions.RegraAmizadeVioladaExcecao;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PedidoAmizade {

  private final UUID id;
  private final UUID remetenteId;
  private final UUID destinatarioId;
  private StatusPedidoAmizade status;
  private final Instant enviadoEm;
  private Instant respondidoEm;

  public PedidoAmizade(UUID id, UUID remetenteId, UUID destinatarioId,
                       StatusPedidoAmizade status, Instant enviadoEm, Instant respondidoEm) {
    this.id = Objects.requireNonNull(id, "O id nao pode ser nulo");
    this.remetenteId = Objects.requireNonNull(remetenteId, "O remetenteId nao pode ser nulo");
    this.destinatarioId = Objects.requireNonNull(destinatarioId, "O destinatarioId nao pode ser nulo");
    this.status = Objects.requireNonNull(status, "O status nao pode ser nulo");
    this.enviadoEm = Objects.requireNonNull(enviadoEm, "O enviadoEm nao pode ser nulo");
    this.respondidoEm = respondidoEm;

    if (remetenteId.equals(destinatarioId)) {
      throw new RegraAmizadeVioladaExcecao("Nao e permitido enviar pedido de amizade para si mesmo");
    }
  }

  public static PedidoAmizade criarNovo(UUID id, UUID remetenteId, UUID destinatarioId, Instant agora) {
    Objects.requireNonNull(agora, "O timestamp de envio nao pode ser nulo");
    return new PedidoAmizade(id, remetenteId, destinatarioId, StatusPedidoAmizade.PENDENTE, agora, null);
  }

  public void aceitar(UUID usuarioSolicitante, Instant agora) {

    validarDestinatario(usuarioSolicitante);
    validarEstadoPendente();

    Objects.requireNonNull(agora, "O timestamp de resposta nao pode ser nulo");

    this.status = StatusPedidoAmizade.ACEITO;
    this.respondidoEm = agora;
  }

  public void recusar(UUID usuarioSolicitante, Instant agora) {

    validarDestinatario(usuarioSolicitante);
    validarEstadoPendente();

    Objects.requireNonNull(agora, "O timestamp de resposta nao pode ser nulo");

    this.status = StatusPedidoAmizade.RECUSADO;
    this.respondidoEm = agora;
  }

  public void cancelar(UUID usuarioSolicitante, Instant agora) {

    if (!this.remetenteId.equals(usuarioSolicitante)) {
      throw new RecursoNaoEncontradoException("Pedido de amizade nao encontrado");
    }
    validarEstadoPendente();

    Objects.requireNonNull(agora, "O timestamp de resposta nao pode ser nulo");

    this.status = StatusPedidoAmizade.CANCELADO;
    this.respondidoEm = agora;
  }

  private void validarDestinatario(UUID usuarioSolicitante) {
    if (!this.destinatarioId.equals(usuarioSolicitante)) {
      throw new RecursoNaoEncontradoException("Pedido de amizade nao encontrado");
    }
  }

  private void validarEstadoPendente() {
    if (this.status != StatusPedidoAmizade.PENDENTE) {
      throw new ConflitoException("O pedido nao pode ser alterado pois ja se encontra no status: " + this.status);
    }
  }

  public UUID getId() {
    return id;
  }

  public UUID getRemetenteId() {
    return remetenteId;
  }

  public UUID getDestinatarioId() {
    return destinatarioId;
  }

  public StatusPedidoAmizade getStatus() {
    return status;
  }

  public Instant getEnviadoEm() {
    return enviadoEm;
  }

  public Instant getRespondidoEm() {
    return respondidoEm;
  }
}
