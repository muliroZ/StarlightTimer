package dev.amigoes.starlighttimer.domain.social.entities;

import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;

import java.time.ZoneId;
import java.util.UUID;

public class Perfil {
    private final UUID usuarioId;
    private String nomeExibido;
    private String avatar;
    private String bio;
    private String fusoHorario;
    private int xpTotal;

    public Perfil(UUID usuarioId, String nomeExibido, String avatar, String bio, String fusoHorario, int xpTotal) {
        this.usuarioId = usuarioId;
        this.nomeExibido = nomeExibido;
        this.avatar = avatar;
        this.bio = bio;
        setFusoHorario(fusoHorario);
        this.xpTotal = xpTotal;
    }

    public void setFusoHorario(String fusoHorario) {
        if (fusoHorario == null || fusoHorario.isBlank()) {
            throw new DomainValidationException("O fuso horário nao pode ser nulo ou vazio");
        }
        try {
            ZoneId.of(fusoHorario);
        } catch (Exception e) {
            throw new DomainValidationException("Fuso horario invalido");
        }
        this.fusoHorario = fusoHorario;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getNomeExibido() {
        return nomeExibido;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBio() {
        return bio;
    }

    public String getFusoHorario() {
        return fusoHorario;
    }

    public int getXpTotal() {
        return xpTotal;
    }
}