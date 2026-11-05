package dev.amigoes.starlighttimer.domain.shared.exceptions;
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
