package dev.amigoes.starlighttimer.records.annotation;

import dev.amigoes.starlighttimer.core.annotation.domain.Anotacao;

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
