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
@Table(name = "configuracoes_usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConfiguracoesUsuarioEntity {

    @Id
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "foco_em_segundos")
    private Integer focoEmSegundos;

    @Column(name = "pausa_em_segundos")
    private Integer pausaEmSegundos;

    @Column(name = "iniciar_pausa_auto")
    private Boolean iniciarPausaAuto;

    @Column(name = "som_habilitado")
    private Boolean somHabilitado;
}
