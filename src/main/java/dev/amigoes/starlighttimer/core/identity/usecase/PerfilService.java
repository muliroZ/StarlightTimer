package dev.amigoes.starlighttimer.core.identity.usecase;

import dev.amigoes.starlighttimer.core.identity.currentuser.UsuarioAtualPort;
import dev.amigoes.starlighttimer.core.identity.domain.Perfil;
import dev.amigoes.starlighttimer.core.identity.persistence.PerfilRepository;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PerfilService {

    private final PerfilRepository perfis;
    private final UsuarioAtualPort usuarioAtualPort;

    public PerfilService(PerfilRepository perfis, UsuarioAtualPort usuarioAtualPort) {
        this.perfis = perfis;
        this.usuarioAtualPort = usuarioAtualPort;
    }

    @Transactional(readOnly = true)
    public Perfil obterPerfil() {
        UUID usuarioId = usuarioAtualPort.getUsuarioId();
        return perfis.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado para o usuário informado."));
    }

    @Transactional
    public Perfil atualizarPerfil(String nomeExibido, String avatar, String bio, String fusoHorario) {
        Perfil perfilExistente = obterPerfil();

        String novoNome = nomeExibido != null ? nomeExibido : perfilExistente.getNomeExibido();
        String novoAvatar = avatar != null ? avatar : perfilExistente.getAvatar();
        String novaBio = bio != null ? bio : perfilExistente.getBio();
        String novoFuso = fusoHorario != null ? fusoHorario : perfilExistente.getFusoHorario();

        Perfil perfilAtualizado = new Perfil(
                perfilExistente.getUsuarioId(),
                novoNome,
                novoAvatar,
                novaBio,
                novoFuso,
                perfilExistente.getXpTotal()
        );

        return perfis.salvar(perfilAtualizado);
    }
}
