package dev.amigoes.starlighttimer.adapters.identity.out.persistence;

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
@Table(name = "perfil")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PerfilEntity {

    @Id
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "nome_exibido", nullable = false, length = 50)
    private String nomeExibido;

    @Column(name = "avatar", columnDefinition = "text")
    private String avatar;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "fuso_horario", nullable = false, length = 40)
    private String fusoHorario;

    @Column(name = "xp_total")
    private Integer xpTotal;
}
