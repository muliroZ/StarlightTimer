package dev.amigoes.starlighttimer.events.kafka.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.amigoes.starlighttimer.enums.session.MotivoAbandonoSessao;
import dev.amigoes.starlighttimer.enums.session.TipoSessao;

import java.util.UUID;

public record SessaoAbandonadaPayloadV1(
        @JsonProperty("usuario_id") UUID usuarioId,
        @JsonProperty("tipo_sessao") TipoSessao tipoSessao,
        @JsonProperty("motivo") MotivoAbandonoSessao motivo
) {}
