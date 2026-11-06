package dev.amigoes.starlighttimer.domain.identity.exceptions;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("E-mail ja cadastrado");
    }

    public EmailJaCadastradoException(Throwable causa) {
        super("E-mail ja cadastrado", causa);
    }
}