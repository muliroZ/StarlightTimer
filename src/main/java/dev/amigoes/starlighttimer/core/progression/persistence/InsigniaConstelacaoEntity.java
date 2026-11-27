package dev.amigoes.starlighttimer.core.progression.persistence;

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
