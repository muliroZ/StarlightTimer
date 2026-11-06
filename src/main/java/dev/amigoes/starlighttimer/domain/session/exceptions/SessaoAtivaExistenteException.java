package dev.amigoes.starlighttimer.domain.session.exceptions;

import java.util.UUID;

public class SessaoAtivaExistenteException extends RuntimeException {
    private final UUID sessaoId;

    public SessaoAtivaExistenteException(UUID sessaoId) {
        super("Ja existe uma sessao ativa");
        this.sessaoId = sessaoId;
    }

    public UUID getSessaoId() {
        return sessaoId;
    }
}
