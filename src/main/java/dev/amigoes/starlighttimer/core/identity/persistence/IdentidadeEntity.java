package dev.amigoes.starlighttimer.core.identity.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "identidade")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IdentidadeEntity {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false, length = 20)
    private String provedor;

    @Column(name = "id_externo", columnDefinition = "text")
    private String idExterno;

    @Column(name = "senha_hash", columnDefinition = "text")
    private String senhaHash;
}
