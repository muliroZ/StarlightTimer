package dev.amigoes.starlighttimer.domain.session.exceptions;

public class PausaExcedidaException extends RuntimeException {
    public PausaExcedidaException(String message) {
        super(message);
    }

    public PausaExcedidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
