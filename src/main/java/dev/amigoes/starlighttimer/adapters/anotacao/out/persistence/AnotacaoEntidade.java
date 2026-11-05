package dev.amigoes.starlighttimer.adapters.anotacao.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "anotacao")
public class AnotacaoEntidade {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "sessao_id")
    private UUID sessaoId;

    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "data_anotacao", nullable = false)
    private LocalDate dataAnotacao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public AnotacaoEntidade() {
    }

    public AnotacaoEntidade(UUID id, UUID usuarioId, UUID sessaoId, String conteudo, LocalDate dataAnotacao, Instant criadoEm, Instant atualizadoEm) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.sessaoId = sessaoId;
        this.conteudo = conteudo;
        this.dataAnotacao = dataAnotacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
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