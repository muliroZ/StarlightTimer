package dev.amigoes.starlighttimer.adapters.shared.in.web;

import dev.amigoes.starlighttimer.domain.session.exceptions.ConclusaoAntecipadaException;
import dev.amigoes.starlighttimer.domain.session.exceptions.TransicaoInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroDeSessaoController {
    @ExceptionHandler(TransicaoInvalidaException.class)
    public ProblemDetail transicaoInvalida(TransicaoInvalidaException excecao) {
        ProblemDetail problema = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        problema.setTitle("Transição de sessão inválida");
        problema.setDetail(excecao.getMessage());
        return problema;
    }

    @ExceptionHandler(ConclusaoAntecipadaException.class)
    public ProblemDetail conclusaoAntecipada(ConclusaoAntecipadaException excecao) {
        ProblemDetail problema = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problema.setTitle("Conclusão antecipada");
        problema.setDetail(excecao.getMessage());
        problema.setProperty("segundosRestantes", excecao.getSegundosRestantes());
        return problema;
    }
}
