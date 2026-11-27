package dev.amigoes.starlighttimer.core.session.domain;

public class TransicaoInvalidaException extends RuntimeException {
    public TransicaoInvalidaException(String message) {
        super(message);
    }

    public TransicaoInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
