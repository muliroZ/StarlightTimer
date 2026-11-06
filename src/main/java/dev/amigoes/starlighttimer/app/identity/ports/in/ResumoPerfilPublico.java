package dev.amigoes.starlighttimer.app.identity.ports.in;

import java.util.UUID;

public record ResumoPerfilPublico(UUID id, String nomeExibido, String avatar) {
}
