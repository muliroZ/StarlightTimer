package dev.amigoes.starlighttimer.records.annotation;

import jakarta.validation.constraints.NotBlank;

public record EditarAnotacaoRequest(
    @NotBlank(message = "O conteudo da anotacao e obrigatorio")
    String conteudo
) {}
