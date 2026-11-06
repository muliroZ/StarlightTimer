package dev.amigoes.starlighttimer.adapters.shared.in.web;

import dev.amigoes.starlighttimer.domain.session.exceptions.ConclusaoAntecipadaException;
import dev.amigoes.starlighttimer.domain.session.exceptions.TransicaoInvalidaException;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RegraDeNegocioException;
import dev.amigoes.starlighttimer.domain.identity.exceptions.UsuarioNaoAutenticadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoAutenticadoException.class)
    public ProblemDetail handleUsuarioNaoAutenticado(UsuarioNaoAutenticadoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Não Autorizado");
        return problem;
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail handleRegraDeNegocio(RegraDeNegocioException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Erro de Validação de Domínio");
        return problem;
    }

    @ExceptionHandler(TransicaoInvalidaException.class)
    public ProblemDetail transicaoInvalida(TransicaoInvalidaException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problem.setTitle("Transição de sessão inválida");
        return problem;
    }

    @ExceptionHandler(ConclusaoAntecipadaException.class)
    public ProblemDetail conclusaoAntecipada(ConclusaoAntecipadaException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conclusão antecipada");
        problem.setDetail(ex.getMessage());
        problem.setProperty("segundosRestantes", ex.getSegundosRestantes());
        return problem;
    }
}