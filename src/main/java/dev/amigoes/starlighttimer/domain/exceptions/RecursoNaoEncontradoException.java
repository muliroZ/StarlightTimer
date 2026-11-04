package dev.amigoes.starlighttimer.domain.exceptions;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String message) {
        super();
    }

    public RecursoNaoEncontradoException(String message, Throwable cause) {
        super(cause);
    }
}
