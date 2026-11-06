package dev.amigoes.starlighttimer.adapters.annotation.in.web;

import dev.amigoes.starlighttimer.domain.annotation.Anotacao;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AnotacaoResponse(
    UUID id,
    UUID usuarioId,
    UUID sessaoId,
    String conteudo,
    LocalDate dataAnotacao,
    Instant criadoEm,
    Instant atualizadoEm
) {
  public static AnotacaoResponse de(Anotacao dominio) {
    return new AnotacaoResponse(
        dominio.getId(),
        dominio.getUsuarioId(),
        dominio.getSessaoId(),
        dominio.getConteudo(),
        dominio.getDataAnotacao(),
        dominio.getCriadoEm(),
        dominio.getAtualizadoEm()
    );
  }
}