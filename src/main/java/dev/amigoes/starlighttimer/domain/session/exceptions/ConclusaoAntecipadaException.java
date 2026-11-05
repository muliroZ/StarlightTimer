package dev.amigoes.starlighttimer.domain.session.exceptions;

public class ConclusaoAntecipadaException extends RuntimeException {
    private final int segundosRestantes;

    public ConclusaoAntecipadaException(String message) {
        super(message);
        this.segundosRestantes = -1;
    }

    public ConclusaoAntecipadaException(String message, Throwable cause) {
        super(message, cause);
        this.segundosRestantes = -1;
    }

    public ConclusaoAntecipadaException(int segundosRestantes) {
        super("Faltam %d segundos para concluir a sessão".formatted(segundosRestantes));
        this.segundosRestantes = segundosRestantes;
    }

    public int getSegundosRestantes() {
        return segundosRestantes;
    }
}
