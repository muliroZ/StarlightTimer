package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BloqueioId implements Serializable {
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "bloqueado_id", nullable = false)
    private UUID bloqueadoId;
}
