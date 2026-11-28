package dev.amigoes.starlighttimer.records.identity;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email, @NotBlank String senha) {
    @Override
    public String toString() {
        return "LoginRequest";
    }
}
