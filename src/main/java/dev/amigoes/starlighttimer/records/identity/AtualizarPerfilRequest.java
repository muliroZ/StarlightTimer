package dev.amigoes.starlighttimer.records.identity;

public record AtualizarPerfilRequest(
        String nomeExibido,
        String avatar,
        String bio,
        String fusoHorario
) {
}
