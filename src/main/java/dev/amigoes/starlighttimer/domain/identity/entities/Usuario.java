package dev.amigoes.starlighttimer.domain.identity.entities;

import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;

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
        if (id == null) {
            throw new DomainValidationException("O ID do usuario nao pode ser nulo");
        }
        if (perfil != null && !id.equals(perfil.getUsuarioId())) {
            throw new DomainValidationException("O Perfil deve pertencer ao mesmo usuario");
        }
        if (configuracoes != null && !id.equals(configuracoes.getUsuarioId())) {
            throw new DomainValidationException("As ConfiguracoesUsuario devem pertencer ao mesmo usuario");
        }

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