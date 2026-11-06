package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class InsigniaConstelacaoId implements Serializable {
    @Column(name = "constelacao_id") private UUID constelacaoId;
    @Column(name = "insignia_id") private UUID insigniaId;
}
