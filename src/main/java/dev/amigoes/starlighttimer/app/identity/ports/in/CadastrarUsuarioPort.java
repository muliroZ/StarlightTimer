package dev.amigoes.starlighttimer.app.identity.ports.in;

import dev.amigoes.starlighttimer.app.identity.dto.CadastroUsuarioComando;
import dev.amigoes.starlighttimer.domain.identity.entities.Usuario;

public interface CadastrarUsuarioPort {
    Usuario cadastrar(CadastroUsuarioComando comando);
}
