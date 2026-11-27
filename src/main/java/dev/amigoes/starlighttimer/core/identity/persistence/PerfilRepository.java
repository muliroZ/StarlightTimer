package dev.amigoes.starlighttimer.core.identity.persistence;

import dev.amigoes.starlighttimer.core.identity.domain.Perfil;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository extends JpaRepository<PerfilEntity, UUID> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PerfilEntity p SET p.nomeExibido = ?2, p.avatar = ?3, p.bio = ?4, p.fusoHorario = ?5 WHERE p.usuarioId = ?1")
    int atualizarCampos(UUID usuarioId, String nomeExibido, String avatar, String bio, String fusoHorario);

    default Perfil salvar(Perfil perfil) {
        int atualizadas = atualizarCampos(perfil.getUsuarioId(), perfil.getNomeExibido(),
                perfil.getAvatar(), perfil.getBio(), perfil.getFusoHorario());
        if (atualizadas == 0) {
            throw new RecursoNaoEncontradoException("Perfil não encontrado para o usuário informado.");
        }
        return findById(perfil.getUsuarioId())
                .map(PerfilMapper::paraDominio)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado para o usuário informado."));
    }

    default Optional<Perfil> buscarPorUsuarioId(UUID usuarioId) {
        return findById(usuarioId).map(PerfilMapper::paraDominio);
    }
}
