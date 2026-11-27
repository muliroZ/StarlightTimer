package dev.amigoes.starlighttimer.core.session.domain;

public class PausaExcedidaException extends RuntimeException {
    public PausaExcedidaException(String message) {
        super(message);
    }

    public PausaExcedidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
