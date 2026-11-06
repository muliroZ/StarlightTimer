package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ganho_xp")
@EqualsAndHashCode(of = "id")
public class GanhoXpEntity {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "origem_sessao")
    private UUID origemSessao;

    @Column(name = "origem_constelacao")
    private UUID origemConstelacao;

    @Column(name = "origem_insignia")
    private UUID origemInsignia;

    @Column(name = "atribuido_em", columnDefinition = "TIMESTAMPTZ", nullable = false)
    private OffsetDateTime atribuidoEm;
}
