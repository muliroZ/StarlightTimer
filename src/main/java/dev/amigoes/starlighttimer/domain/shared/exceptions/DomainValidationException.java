package dev.amigoes.starlighttimer.domain.shared.exceptions;

/** Compatibilidade com os casos de uso de anotacao que ainda usam este nome. */
public class DomainValidationException extends RegraDeNegocioException {
    public DomainValidationException(String mensagem) {
        super(mensagem);
    }
}
