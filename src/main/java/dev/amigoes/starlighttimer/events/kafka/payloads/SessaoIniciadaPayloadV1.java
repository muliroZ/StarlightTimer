package dev.amigoes.starlighttimer.events.kafka.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.amigoes.starlighttimer.enums.session.TipoSessao;

import java.util.UUID;

public record SessaoIniciadaPayloadV1(
        @JsonProperty("usuario_id") UUID usuarioId,
        @JsonProperty("tipo_sessao") TipoSessao tipoSessao,
        @JsonProperty("duracao_planejada_segundos") int duracaoPlanejadaSegundos
) {}
