package dev.amigoes.starlighttimer.adapters.shared.in.web;

import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;
import dev.amigoes.starlighttimer.domain.identity.exceptions.UsuarioNaoAutenticadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoAutenticadoException.class)
    public ProblemDetail handleUsuarioNaoAutenticado(UsuarioNaoAutenticadoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Não Autorizado");
        problem.setType(URI.create("https://starlighttimer.dev/errors/unauthorized"));
        return problem;
    }

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleDomainValidation(DomainValidationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Erro de Validação de Domínio");
        problem.setType(URI.create("https://starlighttimer.dev/errors/domain-validation"));
        return problem;
    }
}