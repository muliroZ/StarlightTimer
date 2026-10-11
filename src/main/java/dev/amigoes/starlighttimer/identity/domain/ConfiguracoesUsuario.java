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
@Table(name = "configuracoes_usuario")
public class ConfiguracoesUsuario {

    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "foco_em_segundos", nullable = false)
    private Integer focoEmSegundos;

    @Column(name = "pausa_em_segundos", nullable = false)
    private Integer pausaEmSegundos;

    @Builder.Default
    @Column(name = "iniciar_pausa_auto", nullable = false)
    private Boolean iniciarPausaAuto = false;

    @Builder.Default
    @Column(name = "som_habilitado", nullable = false)
    private Boolean somHabilitado = true;
}