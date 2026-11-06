package dev.amigoes.starlighttimer.domain.shared.exceptions;

public class ConflitoException extends RuntimeException {

    public ConflitoException(String message) {
        super();
    }

    public ConflitoException(String message, Throwable cause) {
        super(cause);
    }
}
