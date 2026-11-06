package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.app.progression.dto.HistoricoProgressao;
import dev.amigoes.starlighttimer.app.progression.ports.out.AtualizarXpPerfilPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerHistoricoProgressaoPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.UUID;

@Repository
public class JdbcDadosProgressaoAdapter implements LerHistoricoProgressaoPort, AtualizarXpPerfilPort {
    private final JdbcTemplate jdbc;

    public JdbcDadosProgressaoAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public HistoricoProgressao carregar(UUID usuarioId) {
        String fuso = jdbc.queryForObject(
                "SELECT fuso_horario FROM perfil WHERE usuario_id = ?", String.class, usuarioId);
        var ciclos = jdbc.query("""
                SELECT id, tipo, tempo_de_foco, tempo_de_pausa, dia_local, iniciado_em, terminado_em
                  FROM sessao WHERE usuario_id = ? AND status = 'COMPLETED'
                  ORDER BY iniciado_em, id
                """, (rs, n) -> new HistoricoProgressao.Ciclo(
                rs.getObject("id", UUID.class), "FOCUS".equals(rs.getString("tipo")),
                "SHORT_BREAK".equals(rs.getString("tipo")), rs.getInt("tempo_de_foco"),
                rs.getInt("tempo_de_pausa"), rs.getDate("dia_local").toLocalDate(),
                rs.getObject("iniciado_em", java.time.OffsetDateTime.class).toInstant(),
                rs.getObject("terminado_em", java.time.OffsetDateTime.class).toInstant()), usuarioId);
        var notas = jdbc.query("""
                SELECT a.sessao_id, a.criado_em FROM anotacao a
                WHERE a.usuario_id = ? AND a.criado_em IS NOT NULL
                """, (rs, n) -> new HistoricoProgressao.Nota(
                rs.getObject("sessao_id", UUID.class),
                rs.getObject("criado_em", java.time.OffsetDateTime.class).toInstant()), usuarioId);
        return new HistoricoProgressao(ZoneId.of(fuso), ciclos, notas);
    }

    @Override
    public int bloquearEObterTotal(UUID usuarioId) {
        return jdbc.queryForObject("SELECT xp_total FROM perfil WHERE usuario_id = ? FOR UPDATE",
                Integer.class, usuarioId);
    }

    @Override
    public void adicionar(UUID usuarioId, int quantidade) {
        jdbc.update("UPDATE perfil SET xp_total = xp_total + ? WHERE usuario_id = ?",
                quantidade, usuarioId);
    }
}
