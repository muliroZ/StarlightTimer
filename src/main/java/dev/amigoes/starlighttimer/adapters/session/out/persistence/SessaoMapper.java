package dev.amigoes.starlighttimer.adapters.session.out.persistence;

import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.entities.SessaoEvento;

import java.time.ZoneOffset;
import java.util.Date;

public final class SessaoMapper {

  private SessaoMapper() {
  }

  public static SessaoEntity paraEntidade(Sessao dominio) {
    if (dominio == null) return null;
    return new SessaoEntity(
        dominio.getId(),
        dominio.getUsuarioId(),
        dominio.getTipo(),
        dominio.getStatus(),
        dominio.getDuracaoPlanejada(),
        dominio.getTempoDeFoco(),
        dominio.getTempoDePausa(),
        dominio.getDiaLocalAsLocalDate(),
        dominio.getIniciadoEm(),
        dominio.getTerminadoEm()
    );
  }

  public static Sessao paraDominio(SessaoEntity entidade, SessaoEvento ultimoEventoPausa) {
    if (entidade == null) return null;
    Date diaLocalDate = entidade.getDiaLocal() != null
        ? Date.from(entidade.getDiaLocal().atStartOfDay(ZoneOffset.UTC).toInstant())
        : null;

    return new Sessao(
        entidade.getId(),
        entidade.getUsuarioId(),
        entidade.getTipo(),
        entidade.getStatus(),
        entidade.getDuracaoPlanejada(),
        entidade.getTempoDeFoco(),
        entidade.getTempoDePausa(),
        diaLocalDate,
        entidade.getIniciadoEm(),
        entidade.getTerminadoEm(),
        ultimoEventoPausa != null ? ultimoEventoPausa.ocorridoEm() : null
    );
  }

  public static SessaoEventoEntity paraEntidade(SessaoEvento dominio) {
    if (dominio == null) return null;
    return new SessaoEventoEntity(
        dominio.id(),
        dominio.sessaoId(),
        dominio.payload(),
        dominio.tipoEvento(),
        dominio.ocorridoEm(),
        dominio.reportadoEm(),
        dominio.origem()
    );
  }

  public static SessaoEvento paraDominio(SessaoEventoEntity entidade) {
    if (entidade == null) return null;
    return new SessaoEvento(
        entidade.getId(),
        entidade.getSessaoId(),
        entidade.getPayload(),
        entidade.getTipoEvento(),
        entidade.getOcorridoEm(),
        entidade.getReportadoEm(),
        entidade.getOrigem()
    );
  }
}