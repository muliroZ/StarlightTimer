package dev.amigoes.starlighttimer.adapters.social.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "amizade")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AmizadeEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "usuario1_id", nullable = false)
    private UUID usuario1Id;

    @Column(name = "usuario2_id", nullable = false)
    private UUID usuario2Id;

    @Column(name = "estabelecida_em")
    private Instant estabelecidaEm;
}
