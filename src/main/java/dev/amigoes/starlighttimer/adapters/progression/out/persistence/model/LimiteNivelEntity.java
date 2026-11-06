package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "limite_nivel")
public class LimiteNivelEntity {

    @Id
    private Integer nivel;

    @Column(name = "xp_necessario", nullable = false)
    private Integer xpNecessario;
}
