package dev.amigoes.starlighttimer.records.identity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroContaRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8) String senha,
        @NotBlank @Size(max = 50) String nomeExibido,
        @NotBlank @Size(max = 40) String fusoHorario
) {
    @Override
    public String toString() {
        return "CadastroContaRequest";
    }
}
