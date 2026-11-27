package dev.amigoes.starlighttimer.core.progression.persistence;

import dev.amigoes.starlighttimer.records.progression.DadosPerfilProgressao;
import dev.amigoes.starlighttimer.records.progression.HistoricoProgressao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProgressaoRepository {
    private final JdbcTemplate jdbc;

    public ProgressaoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public HistoricoProgressao carregarHistorico(UUID usuarioId) {
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

    public int bloquearEObterTotal(UUID usuarioId) {
        return jdbc.queryForObject("SELECT xp_total FROM perfil WHERE usuario_id = ? FOR UPDATE",
                Integer.class, usuarioId);
    }

    public void adicionar(UUID usuarioId, int quantidade) {
        jdbc.update("UPDATE perfil SET xp_total = xp_total + ? WHERE usuario_id = ?",
                quantidade, usuarioId);
    }

    public Optional<DadosPerfilProgressao> carregarPerfil(UUID usuarioId) {
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
