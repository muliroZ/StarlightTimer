package dev.amigoes.starlighttimer.core.session.usecase;

import dev.amigoes.starlighttimer.core.session.persistence.SessaoRepository;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class VerificarPosseSessaoService {
    private final SessaoRepository sessoes;

    public VerificarPosseSessaoService(SessaoRepository sessoes) {
        this.sessoes = sessoes;
    }

    @Transactional(readOnly = true)
    public void verificar(UUID usuarioId, UUID sessaoId) {
        sessoes.buscarPorIdEUsuario(sessaoId, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));
    }
}
