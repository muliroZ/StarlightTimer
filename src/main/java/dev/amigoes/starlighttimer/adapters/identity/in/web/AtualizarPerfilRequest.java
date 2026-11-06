package dev.amigoes.starlighttimer.adapters.identity.in.web;

public record AtualizarPerfilRequest(
        String nomeExibido,
        String avatar,
        String bio,
        String fusoHorario
) {
}