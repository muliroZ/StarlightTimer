package dev.amigoes.starlighttimer.app.identity.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.ConsultarIdentidadePort;
import dev.amigoes.starlighttimer.app.identity.ports.in.ResumoPerfilPublico;
import dev.amigoes.starlighttimer.app.identity.ports.out.PerfilRepositoryPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultarIdentidadeService implements ConsultarIdentidadePort {
    private final UsuarioAtualPort usuarioAtual;
    private final UsuarioRepositoryPort usuarios;
    private final PerfilRepositoryPort perfis;

    public ConsultarIdentidadeService(UsuarioAtualPort usuarioAtual, UsuarioRepositoryPort usuarios,
                                     PerfilRepositoryPort perfis) {
        this.usuarioAtual = usuarioAtual;
        this.usuarios = usuarios;
        this.perfis = perfis;
    }

    @Override
    public UUID obterUsuarioAtual() {
        return usuarioAtual.getUsuarioId();
    }

    @Override
    public boolean existeUsuarioAtivo(UUID id) {
        return usuarios.existeAtivoPorId(id);
    }

    @Override
    public ResumoPerfilPublico obterPerfilPublico(UUID id) {
        var perfil = perfis.buscarPorUsuarioId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));
        return new ResumoPerfilPublico(id, perfil.getNomeExibido(), perfil.getAvatar());
    }
}
