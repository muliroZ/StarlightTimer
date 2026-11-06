package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "insignia_constelacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InsigniaConstelacaoEntity {
    @EmbeddedId private InsigniaConstelacaoId id;
}
