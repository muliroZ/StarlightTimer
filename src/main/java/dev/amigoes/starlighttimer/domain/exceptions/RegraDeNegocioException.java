package dev.amigoes.starlighttimer.domain.exceptions;

public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String message) {
        super();
    }

    public RegraDeNegocioException(String message, Throwable cause) {
        super(cause);
    }
}
