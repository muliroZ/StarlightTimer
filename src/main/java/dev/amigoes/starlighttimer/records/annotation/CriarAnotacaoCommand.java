package dev.amigoes.starlighttimer.records.annotation;

import java.time.LocalDate;
import java.util.UUID;

public record CriarAnotacaoCommand(
    UUID sessaoId,
    String conteudo,
    LocalDate dataAnotacao
) {}
