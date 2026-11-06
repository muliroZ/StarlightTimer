package dev.amigoes.starlighttimer.adapters.annotation.in.web;

import jakarta.validation.constraints.NotBlank;

public record EditarAnotacaoRequest(
    @NotBlank(message = "O conteudo da anotacao e obrigatorio")
    String conteudo
) {}