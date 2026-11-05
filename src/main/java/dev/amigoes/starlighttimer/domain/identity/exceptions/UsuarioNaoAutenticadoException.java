package dev.amigoes.starlighttimer.domain.identity.exceptions;
public class UsuarioNaoAutenticadoException extends RuntimeException {
    public UsuarioNaoAutenticadoException(String message) {
        super(message);
    }
}
