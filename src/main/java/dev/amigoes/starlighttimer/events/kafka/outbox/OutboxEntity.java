package dev.amigoes.starlighttimer.events.kafka.outbox;

import dev.amigoes.starlighttimer.events.kafka.TipoAgregado;
import dev.amigoes.starlighttimer.events.kafka.TipoEvento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "outbox")
@EqualsAndHashCode(of = "id")
public class OutboxEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_agregado", length = 25)
    private TipoAgregado tipoAgregado;

    @Column(name = "id_agregado", nullable = false)
    private UUID agregadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", length = 40)
    private TipoEvento tipoEvento;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "publicado_em")
    private OffsetDateTime publicadoEm;

    public OutboxEntity(
            UUID id,
            TipoAgregado tipoAgregado,
            UUID agregadoId,
            TipoEvento tipoEvento,
            String payload,
            OffsetDateTime criadoEm
    ) {
        this.id = id;
        this.tipoAgregado = tipoAgregado;
        this.agregadoId = agregadoId;
        this.tipoEvento = tipoEvento;
        this.payload = payload;
        this.criadoEm = criadoEm;
    }

    public void marcarDataPublicacao(Clock clock) {
        this.publicadoEm = OffsetDateTime.now(clock);
    }
}
