package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.app.progression.dto.DadosPerfilProgressao;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerDadosPerfilProgressaoPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcLerDadosPerfilProgressaoAdapter implements LerDadosPerfilProgressaoPort {
    private final JdbcTemplate jdbc;

    public JdbcLerDadosPerfilProgressaoAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<DadosPerfilProgressao> carregar(UUID usuarioId) {
        var perfis = jdbc.query("SELECT xp_total, fuso_horario FROM perfil WHERE usuario_id = ?",
                (rs, indice) -> new DadosPerfilProgressao(
                        rs.getInt("xp_total"), ZoneId.of(rs.getString("fuso_horario")),
                        jdbc.query("""
                                SELECT insignia_id, contagem_progresso, desbloqueado_em
                                  FROM insignia_usuario WHERE usuario_id = ?
                                """, (linha, item) -> new DadosPerfilProgressao.InsigniaUsuario(
                                linha.getObject("insignia_id", UUID.class),
                                linha.getInt("contagem_progresso"),
                                instante(linha.getObject("desbloqueado_em", OffsetDateTime.class))), usuarioId),
                        jdbc.query("""
                                SELECT constelacao_id, desbloqueado_em
                                  FROM constelacao_usuario WHERE usuario_id = ?
                                """, (linha, item) -> new DadosPerfilProgressao.ConstelacaoUsuario(
                                linha.getObject("constelacao_id", UUID.class),
                                instante(linha.getObject("desbloqueado_em", OffsetDateTime.class))), usuarioId)),
                usuarioId);
        return perfis.stream().findFirst();
    }

    private java.time.Instant instante(OffsetDateTime data) {
        return data == null ? null : data.toInstant();
    }
}
