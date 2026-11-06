package dev.amigoes.starlighttimer.adapters.annotation.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CriarAnotacaoRequest(
    UUID sessaoId,
    @NotBlank(message = "O conteudo da anotacao e obrigatorio")
    String conteudo,
    @NotNull(message = "A data da anotacao e obrigatoria")
    LocalDate dataAnotacao
) {}