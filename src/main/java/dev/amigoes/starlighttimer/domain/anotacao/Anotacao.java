package dev.amigoes.starlighttimer.domain.anotacao;

import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Anotacao {
    private final UUID id;
    private final UUID usuarioId;
    private final UUID sessaoId; // Opcional (pode ser nulo)
    private String conteudo;
    private final LocalDate dataAnotacao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Anotacao(UUID id, UUID usuarioId, UUID sessaoId, String conteudo, LocalDate dataAnotacao, Instant criadoEm, Instant atualizadoEm) {
        if (id == null) {
            throw new DomainValidationException("O ID da anotacao nao pode ser nulo.");
        }
        if (usuarioId == null) {
            throw new DomainValidationException("O ID do usuario e obrigatorio.");
        }
        if (conteudo == null || conteudo.isBlank()) {
            throw new DomainValidationException("O conteudo da anotacao e obrigatorio.");
        }
        if (dataAnotacao == null) {
            throw new DomainValidationException("A data da anotacao e obrigatoria.");
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.sessaoId = sessaoId;
        this.conteudo = conteudo;
        this.dataAnotacao = dataAnotacao;
        this.criadoEm = criadoEm != null ? criadoEm : Instant.now();
        this.atualizadoEm = atualizadoEm != null ? atualizadoEm : this.criadoEm;
    }

    public void atualizarConteudo(String novoConteudo, Instant agora) {
        if (novoConteudo == null || novoConteudo.isBlank()) {
            throw new DomainValidationException("O conteudo da anotacao nao pode ser vazio.");
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