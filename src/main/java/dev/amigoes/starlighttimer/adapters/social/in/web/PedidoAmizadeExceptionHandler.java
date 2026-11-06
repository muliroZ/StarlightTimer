package dev.amigoes.starlighttimer.adapters.social.in.web;

import dev.amigoes.starlighttimer.domain.shared.exceptions.ConflitoException;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.domain.social.exceptions.RegraAmizadeVioladaExcecao;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {PedidoAmizadeController.class, CirculoSocialController.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PedidoAmizadeExceptionHandler {
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail recursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RegraAmizadeVioladaExcecao.class)
    public ProblemDetail regraViolada(RegraAmizadeVioladaExcecao ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
    }
}
