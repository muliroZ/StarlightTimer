package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "constelacao_usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConstelacaoUsuarioEntity {
    @EmbeddedId private ConstelacaoUsuarioId id;
    @Column(name = "desbloqueado_em", columnDefinition = "TIMESTAMPTZ") private Instant desbloqueadoEm;
}
