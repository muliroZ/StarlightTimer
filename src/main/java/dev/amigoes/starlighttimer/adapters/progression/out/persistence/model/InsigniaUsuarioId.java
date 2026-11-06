package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class InsigniaUsuarioId implements Serializable {

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "insignia_id")
    private UUID insigniaId;
}
