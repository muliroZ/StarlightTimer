package dev.amigoes.starlighttimer.records.identity;

public record AtualizarConfiguracoesUsuarioRequest(
        Integer focoEmSegundos,
        Integer pausaEmSegundos,
        Boolean iniciarPausaAuto,
        Boolean somHabilitado
) {
}
