package dev.amigoes.starlighttimer.domain.session.exceptions;

public class TransicaoInvalidaException extends RuntimeException {
    public TransicaoInvalidaException(String message) {
        super(message);
    }

    public TransicaoInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
