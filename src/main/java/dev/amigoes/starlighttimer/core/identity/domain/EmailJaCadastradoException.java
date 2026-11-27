package dev.amigoes.starlighttimer.core.identity.domain;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("E-mail ja cadastrado");
    }

    public EmailJaCadastradoException(Throwable causa) {
        super("E-mail ja cadastrado", causa);
    }
}
