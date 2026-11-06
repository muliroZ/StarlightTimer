package dev.amigoes.starlighttimer.adapters.identity.in.web;

public record AtualizarConfiguracoesUsuarioRequest(
        Integer focoEmSegundos,
        Integer pausaEmSegundos,
        Boolean iniciarPausaAuto,
        Boolean somHabilitado
) {
}