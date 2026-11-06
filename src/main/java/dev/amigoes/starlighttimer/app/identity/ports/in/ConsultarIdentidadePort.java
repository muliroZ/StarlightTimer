package dev.amigoes.starlighttimer.app.identity.ports.in;

import java.util.UUID;

public interface ConsultarIdentidadePort {
    UUID obterUsuarioAtual();
    boolean existeUsuarioAtivo(UUID id);
}
