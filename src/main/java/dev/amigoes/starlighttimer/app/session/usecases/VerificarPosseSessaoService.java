package dev.amigoes.starlighttimer.app.session.usecases;

import dev.amigoes.starlighttimer.app.session.ports.in.VerificarPosseSessaoUseCase;
import dev.amigoes.starlighttimer.app.session.ports.out.SessaoRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class VerificarPosseSessaoService implements VerificarPosseSessaoUseCase {
    private final SessaoRepositoryPort sessoes;

    public VerificarPosseSessaoService(SessaoRepositoryPort sessoes) {
        this.sessoes = sessoes;
    }

    @Override
    @Transactional(readOnly = true)
    public void verificar(UUID usuarioId, UUID sessaoId) {
        sessoes.buscarPorIdEUsuario(sessaoId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));
    }
}
