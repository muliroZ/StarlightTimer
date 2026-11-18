package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.app.progression.ports.out.LerResumoDiaPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class JdbcLerResumoDiaAdapter implements LerResumoDiaPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLerResumoDiaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public EstatisticasHoje obterEstatisticasDoDia(UUID usuarioId, LocalDate diaLocal) {
        String sql = """
                SELECT 
                    COUNT(*) AS total_estrelas,
                    COALESCE(SUM(tempo_de_foco), 0) AS total_tempo_foco
                FROM sessao
                WHERE usuario_id = ?
                  AND dia_local = ?
                  AND tipo = 'FOCO'
                  AND status = 'COMPLETED'
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new EstatisticasHoje(
                rs.getInt("total_estrelas"),
                rs.getInt("total_tempo_foco")
        ), usuarioId, Date.valueOf(diaLocal));
    }

    @Override
    public List<LocalDate> obterDiasComFocoConcluido(UUID usuarioId) {
        String sql = """
                SELECT DISTINCT dia_local
                FROM sessao
                WHERE usuario_id = ?
                  AND tipo = 'FOCO'
                  AND status = 'COMPLETED'
                ORDER BY dia_local DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getDate("dia_local").toLocalDate(), usuarioId);
    }
}