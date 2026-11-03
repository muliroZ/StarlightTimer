package dev.amigoes.starlighttimer.domain.social.entities;

import java.time.Instant;
import java.util.UUID;

public class Usuario {
    private final UUID id;
    private String email;
    private String status;
    private final Instant criadoEm;
    private Instant excluidoEm;
    private Perfil perfil;
    private ConfiguracoesUsuario configuracoes;

    public Usuario(UUID id, String email, String status, Instant criadoEm, Instant excluidoEm, Perfil perfil, ConfiguracoesUsuario configuracoes) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.criadoEm = criadoEm;
        this.excluidoEm = excluidoEm;
        this.perfil = perfil;
        this.configuracoes = configuracoes;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getExcluidoEm() {
        return excluidoEm;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public ConfiguracoesUsuario getConfiguracoes() {
        return configuracoes;
    }
}