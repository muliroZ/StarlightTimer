package dev.amigoes.starlighttimer.adapters.identity.in.web;

import dev.amigoes.starlighttimer.domain.identity.exceptions.EmailJaCadastradoException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.URI;

@RestControllerAdvice(basePackages = "dev.amigoes.starlighttimer.adapters.identity")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CadastroUsuarioExceptionHandler {
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ProblemDetail handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("E-mail ja cadastrado");
        problem.setType(URI.create("https://starlighttimer.dev/errors/email-ja-cadastrado"));
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleDadosInvalidos(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Dados de cadastro invalidos");
        problem.setTitle("Erro de validacao do cadastro");
        problem.setType(URI.create("https://starlighttimer.dev/errors/validation"));
        return problem;
    }
}
