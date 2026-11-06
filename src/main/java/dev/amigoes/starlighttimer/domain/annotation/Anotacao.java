package dev.amigoes.starlighttimer.domain.annotation;

import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Anotacao {
    private final UUID id;
    private final UUID usuarioId;
    private final UUID sessaoId;
    private String conteudo;
    private final LocalDate dataAnotacao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Anotacao(UUID id, UUID usuarioId, UUID sessaoId, String conteudo, LocalDate dataAnotacao, Instant criadoEm, Instant atualizadoEm) {
        if (id == null) {
            throw new DomainValidationException("O ID da anotação não pode ser nulo.");
        }
        if (usuarioId == null) {
            throw new DomainValidationException("O ID do usuário é obrigatório.");
        }
        if (conteudo == null || conteudo.isBlank()) {
            throw new DomainValidationException("O conteúdo da anotação é obrigatório.");
        }
        if (dataAnotacao == null) {
            throw new DomainValidationException("A data da anotação é obrigatória.");
        }
        if (criadoEm == null) {
            throw new DomainValidationException("O instante de criação da anotação é obrigatório.");
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.sessaoId = sessaoId;
        this.conteudo = conteudo;
        this.dataAnotacao = dataAnotacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm != null ? atualizadoEm : criadoEm;
    }

    public void atualizarConteudo(String novoConteudo, Instant agora) {
        if (novoConteudo == null || novoConteudo.isBlank()) {
            throw new DomainValidationException("O conteúdo da anotação não pode ser vazio.");
        }
        if (agora == null) {
            throw new DomainValidationException("O instante de atualização é obrigatório.");
        }
        this.conteudo = novoConteudo;
        this.atualizadoEm = agora;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getSessaoId() {
        return sessaoId;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDate getDataAnotacao() {
        return dataAnotacao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}