package dev.amigoes.starlighttimer.adapters.session.out.persistence;

import dev.amigoes.starlighttimer.domain.session.enums.OrigemEvento;
import dev.amigoes.starlighttimer.domain.session.enums.TipoEvento;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessao_evento")
public class SessaoEntity {

  @Id
  private UUID id;

  @Column(name = "sessao_id", nullable = false)
  private UUID sessaoId;

  @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_evento", nullable = false, length = 25)
  private TipoEvento tipoEvento;

  @Column(name = "ocorrido_em", nullable = false)
  private OffsetDateTime ocorridoEm;

  @Column(name = "reportado_em")
  private OffsetDateTime reportadoEm;

  @Enumerated(EnumType.STRING)
  @Column(name = "origem", nullable = false, length = 25)
  private OrigemEvento origem;

  protected SessaoEventoEntity() {
  }

  public SessaoEventoEntity(UUID id, UUID sessaoId, String payload, TipoEvento tipoEvento,
                            OffsetDateTime ocorridoEm, OffsetDateTime reportadoEm, OrigemEvento origem) {
    this.id = id;
    this.sessaoId = sessaoId;
    this.payload = payload;
    this.tipoEvento = tipoEvento;
    this.ocorridoEm = ocorridoEm;
    this.reportadoEm = reportadoEm;
    this.origem = origem;
  }
  public UUID getId() {
    return id;
  }

  public UUID getSessaoId() {
    return sessaoId;
  }

  public String getPayload() {
    return payload;
  }

  public TipoEvento getTipoEvento() {
    return tipoEvento;
  }

  public OffsetDateTime getOcorridoEm() {
    return ocorridoEm;
  }

  public OffsetDateTime getReportadoEm() {
    return reportadoEm;
  }

  public OrigemEvento getOrigem() {
    return origem;
  }

}
