package dev.amigoes.starlighttimer.core.identity.usecase;

import dev.amigoes.starlighttimer.core.identity.currentuser.UsuarioAtualPort;
import dev.amigoes.starlighttimer.core.identity.persistence.PerfilRepository;
import dev.amigoes.starlighttimer.core.identity.persistence.UsuarioRepository;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.records.identity.ResumoPerfilPublico;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultarIdentidadeService {
    private final UsuarioAtualPort usuarioAtual;
    private final UsuarioRepository usuarios;
    private final PerfilRepository perfis;

    public ConsultarIdentidadeService(UsuarioAtualPort usuarioAtual, UsuarioRepository usuarios,
                                     PerfilRepository perfis) {
        this.usuarioAtual = usuarioAtual;
        this.usuarios = usuarios;
        this.perfis = perfis;
    }

    public UUID obterUsuarioAtual() {
        return usuarioAtual.getUsuarioId();
    }

    public boolean existeUsuarioAtivo(UUID id) {
        return usuarios.existeAtivoPorId(id);
    }

    public ResumoPerfilPublico obterPerfilPublico(UUID id) {
        var perfil = perfis.buscarPorUsuarioId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));
        return new ResumoPerfilPublico(id, perfil.getNomeExibido(), perfil.getAvatar());
    }
}
