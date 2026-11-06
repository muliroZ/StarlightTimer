package dev.amigoes.starlighttimer.app.annotation.ports.in;

import java.time.LocalDate;
import java.util.UUID;

public record CriarAnotacaoCommand(
    UUID sessaoId,
    String conteudo,
    LocalDate dataAnotacao
) {}