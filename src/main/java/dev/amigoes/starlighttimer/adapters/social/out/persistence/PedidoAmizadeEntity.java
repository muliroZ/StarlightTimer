package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import dev.amigoes.starlighttimer.domain.social.enums.StatusPedidoAmizade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pedido_amizade")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PedidoAmizadeEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "remetente_id", nullable = false)
    private UUID remetenteId;

    @Column(name = "destinatario_id", nullable = false)
    private UUID destinatarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusPedidoAmizade status;

    @Column(name = "enviado_em")
    private Instant enviadoEm;

    @Column(name = "respondido_em")
    private Instant respondidoEm;
}
