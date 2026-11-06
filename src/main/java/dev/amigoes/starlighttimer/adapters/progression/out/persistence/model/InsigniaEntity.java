package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import dev.amigoes.starlighttimer.domain.progression.enums.TipoCriterio;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "insignia")
public class InsigniaEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(unique = true, nullable = false, length = 50)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String icone;

    @Column(name = "xp_recompensa", nullable = false)
    private Integer xpRecompensa;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "criterio_json", columnDefinition = "jsonb", nullable = false)
    private String criterioJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_criterio")
    private TipoCriterio tipoCriterio;

    @Column(name = "contagem_alvo", nullable = false)
    private Integer contagemAlvo;
}
