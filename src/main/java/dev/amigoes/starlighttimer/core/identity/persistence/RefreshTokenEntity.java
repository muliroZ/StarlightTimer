package dev.amigoes.starlighttimer.core.identity.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshTokenEntity {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "token_hash", nullable = false, columnDefinition = "text")
    private String tokenHash;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "criado_em")
    private Instant criadoEm;

    @Column(name = "revogado_em")
    private Instant revogadoEm;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    public boolean estaAtivoEm(Instant agora) {
        return revogadoEm == null && expiraEm.isAfter(agora);
    }
}
