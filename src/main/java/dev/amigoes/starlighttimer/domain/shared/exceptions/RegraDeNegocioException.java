package dev.amigoes.starlighttimer.domain.shared.exceptions;

public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String message) {
        super();
    }

    public RegraDeNegocioException(String message, Throwable cause) {
        super(cause);
    }
}
