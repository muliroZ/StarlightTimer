package dev.amigoes.starlighttimer.app.identity.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.AtualizarPerfilUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.ObterPerfilUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.PerfilRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;
import dev.amigoes.starlighttimer.domain.social.entities.Perfil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PerfilService implements ObterPerfilUseCase, AtualizarPerfilUseCase {

    private final PerfilRepositoryPort perfilRepositoryPort;
    private final UsuarioAtualPort usuarioAtualPort;

    public PerfilService(PerfilRepositoryPort perfilRepositoryPort, UsuarioAtualPort usuarioAtualPort) {
        this.perfilRepositoryPort = perfilRepositoryPort;
        this.usuarioAtualPort = usuarioAtualPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil obterPerfil() {
        UUID usuarioId = usuarioAtualPort.obterUsuarioIdAtual();
        return perfilRepositoryPort.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new DomainValidationException("Perfil não encontrado para o usuário informado."));
    }

    @Override
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

        return perfilRepositoryPort.salvar(perfilAtualizado);
    }
}