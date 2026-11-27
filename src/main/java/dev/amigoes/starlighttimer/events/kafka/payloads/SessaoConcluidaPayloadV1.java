package dev.amigoes.starlighttimer.events.kafka.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.amigoes.starlighttimer.enums.session.TipoSessao;

import java.util.UUID;

public record SessaoConcluidaPayloadV1(
        @JsonProperty("usuario_id") UUID usuarioId,
        @JsonProperty("tipo_sessao") TipoSessao tipoSessao,
        @JsonProperty("tempo_de_foco_segundos") int tempoDeFocoSegundos
) {}
