package dev.amigoes.starlighttimer.domain.shared.exceptions;
public class UsuarioNaoAutenticadoException extends RuntimeException {
    public UsuarioNaoAutenticadoException(String message) {
        super(message);
    }
}
