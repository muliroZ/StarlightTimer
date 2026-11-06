package dev.amigoes.starlighttimer.app.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioComando(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 50) String nomeExibido,
        @NotBlank @Size(max = 40) String fusoHorario
) {
}
