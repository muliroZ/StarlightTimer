package dev.amigoes.starlighttimer.core.session.persistence;

import dev.amigoes.starlighttimer.enums.session.OrigemEvento;
import dev.amigoes.starlighttimer.enums.session.TipoEvento;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessao_evento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SessaoEventoEntity {

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

}
