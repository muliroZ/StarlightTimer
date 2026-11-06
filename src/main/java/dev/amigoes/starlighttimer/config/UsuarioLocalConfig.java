package dev.amigoes.starlighttimer.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Configuration
@Profile("local")
public class UsuarioLocalConfig {
    public static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Bean
    ApplicationRunner criarUsuarioLocalPadrao(JdbcTemplate jdbc, TransactionTemplate transacao) {
        return args -> transacao.executeWithoutResult(status -> {
            jdbc.update("""
                    INSERT INTO usuario (id, email, status)
                    VALUES (?, 'local@starlighttimer.test', 'ACTIVE')
                    ON CONFLICT (id) DO NOTHING
                    """, USUARIO_ID);
            jdbc.update("""
                    INSERT INTO perfil (usuario_id, nome_exibido, fuso_horario)
                    VALUES (?, 'Usuário local', 'America/Recife')
                    ON CONFLICT (usuario_id) DO NOTHING
                    """, USUARIO_ID);
            jdbc.update("""
                    INSERT INTO configuracoes_usuario (usuario_id)
                    VALUES (?)
                    ON CONFLICT (usuario_id) DO NOTHING
                    """, USUARIO_ID);
        });
    }
}
