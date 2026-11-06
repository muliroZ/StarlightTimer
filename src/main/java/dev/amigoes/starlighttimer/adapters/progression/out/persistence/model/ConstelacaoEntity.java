package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "constelacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConstelacaoEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 30) private String codigo;
    @Column(nullable = false, unique = true, length = 30) private String nome;
    @Column(name = "xp_recompensa", nullable = false) private Integer xpRecompensa;
    @Column(name = "qtd_estrelas", nullable = false) private Integer qtdEstrelas;
    @Column(name = "qtd_insignias_requeridas", nullable = false) private Integer qtdInsigniasRequeridas;
    @Column(columnDefinition = "TEXT") private String descricao;
    @Column(name = "asset_svg", columnDefinition = "TEXT") private String assetSvg;
}
