package dev.amigoes.starlighttimer.identity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "identidade")
public class Identidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 20)
    private String provedor;

    @Column(name = "id_externo", columnDefinition = "TEXT")
    private String idExterno;

    @Column(name = "senha_hash", columnDefinition = "TEXT")
    private String senhaHash;
}