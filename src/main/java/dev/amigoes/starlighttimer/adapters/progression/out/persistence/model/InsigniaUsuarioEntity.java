package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "insignia_usuario")
public class InsigniaUsuarioEntity {

    @EmbeddedId
    private InsigniaUsuarioId id;

    @Column(name = "contagem_progresso", nullable = false)
    private Integer contagemProgresso;

    @Column(name = "desbloqueado_em", columnDefinition = "TIMESTAMPTZ")
    private Instant desbloqueadoEm;
}
