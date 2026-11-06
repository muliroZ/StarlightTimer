package dev.amigoes.starlighttimer.adapters.session.out.persistence;

import dev.amigoes.starlighttimer.domain.session.enums.StatusSessao;
import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessao")
public class SessaoEntity {

  @Id
  private UUID id;

  @Column(name = "usuario_id", nullable = false)
  private UUID usuarioId;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false, length = 25)
  private TipoSessao tipo;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private StatusSessao status;

  @Column(name = "duracao_planejada", nullable = false)
  private int duracaoPlanejada;

  @Column(name = "tempo_de_foco", nullable = false)
  private int tempoDeFoco;

  @Column(name = "tempo_de_pausa", nullable = false)
  private int tempoDePausa;

  @Column(name = "dia_local", nullable = false)
  private LocalDate diaLocal;

  @Column(name = "iniciado_em", nullable = false)
  private OffsetDateTime iniciadoEm;

  @Column(name = "terminado_em")
  private OffsetDateTime terminadoEm;

  protected SessaoEntity() {
  }

  public SessaoEntity(UUID id, UUID usuarioId, TipoSessao tipo, StatusSessao status,
                      int duracaoPlanejada, int tempoDeFoco, int tempoDePausa,
                      LocalDate diaLocal, OffsetDateTime iniciadoEm, OffsetDateTime terminadoEm) {
    this.id = id;
    this.usuarioId = usuarioId;
    this.tipo = tipo;
    this.status = status;
    this.duracaoPlanejada = duracaoPlanejada;
    this.tempoDeFoco = tempoDeFoco;
    this.tempoDePausa = tempoDePausa;
    this.diaLocal = diaLocal;
    this.iniciadoEm = iniciadoEm;
    this.terminadoEm = terminadoEm;
  }

  public UUID getId() {return id;}

  public UUID getUsuarioId() {return usuarioId;}

  public TipoSessao getTipo() {return tipo;}

  public StatusSessao getStatus() {return status;}

  public int getDuracaoPlanejada() {return duracaoPlanejada;}

  public int getTempoDeFoco() {return tempoDeFoco;}

  public int getTempoDePausa() {return tempoDePausa;}

  public LocalDate getDiaLocal() {return diaLocal;}

  public OffsetDateTime getIniciadoEm() {return iniciadoEm;}

  public OffsetDateTime getTerminadoEm() {return terminadoEm;}
}