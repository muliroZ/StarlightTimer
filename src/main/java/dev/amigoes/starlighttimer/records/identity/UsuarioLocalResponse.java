package dev.amigoes.starlighttimer.records.identity;

import java.util.UUID;

public record UsuarioLocalResponse(UUID id, String email, String nomeExibido, String fusoHorario) {}
