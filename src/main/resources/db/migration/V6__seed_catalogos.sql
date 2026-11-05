INSERT INTO limite_nivel (nivel, xp_necessario)
SELECT n, 50 * (n - 1) + 25 * (n - 1) * (n - 2) / 2
FROM generate_series(1, 60) AS niveis(n);

INSERT INTO patente (
    id, codigo, nome, nivel_minimo
) VALUES
    ('10000000-0000-4000-8000-000000000001', 'STARGAZER', 'Stargazer', 1),
    ('10000000-0000-4000-8000-000000000002', 'STAR_SEEKER', 'Star Seeker', 6),
    ('10000000-0000-4000-8000-000000000003', 'ASTRAL_VOYAGER', 'Astral Voyager', 11),
    ('10000000-0000-4000-8000-000000000004', 'STELLAR_NAVIGATOR', 'Stellar Navigator', 16),
    ('10000000-0000-4000-8000-000000000005', 'STELLAR_CARTOGRAPHER', 'Stellar Cartographer', 21),
    ('10000000-0000-4000-8000-000000000006', 'STARFORGER', 'Starforger', 26),
    ('10000000-0000-4000-8000-000000000007', 'CELESTIAL_WARDEN', 'Celestial Warden', 31),
    ('10000000-0000-4000-8000-000000000008', 'NOVA_COMMANDER', 'Nova Commander', 36),
    ('10000000-0000-4000-8000-000000000009', 'ASTRAL_SOVEREIGN', 'Astral Sovereign', 41),
    ('10000000-0000-4000-8000-000000000010', 'QUASAR_SOVEREIGN', 'Quasar Sovereign', 46),
    ('10000000-0000-4000-8000-000000000011', 'CELESTIAL_ARCHITECT', 'Celestial Architect', 51),
    ('10000000-0000-4000-8000-000000000012', 'COSMIC_ARCHITECT', 'Cosmic Architect', 56);

INSERT INTO insignia (
    id, codigo, nome, descricao, xp_recompensa, criterio_json, tipo_criterio, contagem_alvo
) VALUES
    ('20000000-0000-4000-8000-000000000001', 'BADGE_FIRST_LIGHT', 'First Light', 'Concluir 1 ciclos de foco.', 50, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"}}', 'CONTAGEM_SESSOES', 1),
    ('20000000-0000-4000-8000-000000000002', 'BADGE_IGNITION_SEQUENCE', 'Ignition Sequence', 'Concluir 10 ciclos de foco.', 50, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"}}', 'CONTAGEM_SESSOES', 10),
    ('20000000-0000-4000-8000-000000000003', 'BADGE_SUPERNOVA', 'Supernova', 'Concluir 50 ciclos de foco.', 150, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"}}', 'CONTAGEM_SESSOES', 50),
    ('20000000-0000-4000-8000-000000000004', 'BADGE_STELLAR_FORGE', 'Stellar Forge', 'Concluir 250 ciclos de foco.', 400, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"}}', 'CONTAGEM_SESSOES', 250),
    ('20000000-0000-4000-8000-000000000005', 'BADGE_A_THOUSAND_SUNS', 'A Thousand Suns', 'Concluir 1000 ciclos de foco.', 1000, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"}}', 'CONTAGEM_SESSOES', 1000),
    ('20000000-0000-4000-8000-000000000006', 'BADGE_SILENT_CORE', 'Silent Core', 'Concluir 10 ciclos de foco sem pausa.', 50, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED","tempo_de_pausa":0}}', 'CONTAGEM_SESSOES', 10),
    ('20000000-0000-4000-8000-000000000007', 'BADGE_NIGHT_WATCH', 'Night Watch', 'Concluir 10 focos iniciados entre 00:00 (inclusive) e 04:00 (exclusive), no fuso do perfil.', 150, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"janela_hora_local":{"campo":"iniciado_em","inicio_inclusivo":"00:00","fim_exclusivo":"04:00","fuso":"PERFIL_NA_AVALIACAO"}}', 'CONTAGEM_SESSOES', 10),
    ('20000000-0000-4000-8000-000000000008', 'BADGE_DAWNBRINGER', 'Dawnbringer', 'Concluir 10 focos iniciados entre 05:00 (inclusive) e 08:00 (exclusive), no fuso do perfil.', 150, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"janela_hora_local":{"campo":"iniciado_em","inicio_inclusivo":"05:00","fim_exclusivo":"08:00","fuso":"PERFIL_NA_AVALIACAO"}}', 'CONTAGEM_SESSOES', 10),
    ('20000000-0000-4000-8000-000000000009', 'BADGE_MERIDIAN', 'Meridian', 'Concluir 10 focos iniciados entre 11:00 (inclusive) e 14:00 (exclusive), no fuso do perfil.', 150, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"janela_hora_local":{"campo":"iniciado_em","inicio_inclusivo":"11:00","fim_exclusivo":"14:00","fuso":"PERFIL_NA_AVALIACAO"}}', 'CONTAGEM_SESSOES', 10),
    ('20000000-0000-4000-8000-000000000010', 'BADGE_DEEP_FIELD', 'Deep Field', 'Acumular 1500 minutos de foco em ciclos concluidos.', 150, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"campo_soma":"tempo_de_foco","unidade_origem":"SEGUNDOS","unidade_alvo":"MINUTOS"}', 'MINUTOS_FOCO', 1500),
    ('20000000-0000-4000-8000-000000000011', 'BADGE_EVENT_HORIZON', 'Event Horizon', 'Acumular 30000 minutos de foco em ciclos concluidos.', 1000, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"campo_soma":"tempo_de_foco","unidade_origem":"SEGUNDOS","unidade_alvo":"MINUTOS"}', 'MINUTOS_FOCO', 30000),
    ('20000000-0000-4000-8000-000000000012', 'BADGE_FIRST_ORBIT', 'First Orbit', 'Alcancar orbit de 3 dias logicos consecutivos.', 50, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"campo_dia":"dia_local","agregacao":"MAIOR_SEQUENCIA"}', 'ORBIT', 3),
    ('20000000-0000-4000-8000-000000000013', 'BADGE_STEADY_ORBIT', 'Steady Orbit', 'Alcancar orbit de 7 dias logicos consecutivos.', 150, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"campo_dia":"dia_local","agregacao":"MAIOR_SEQUENCIA"}', 'ORBIT', 7),
    ('20000000-0000-4000-8000-000000000014', 'BADGE_UNBROKEN_ORBIT', 'Unbroken Orbit', 'Alcancar orbit de 30 dias logicos consecutivos.', 400, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"campo_dia":"dia_local","agregacao":"MAIOR_SEQUENCIA"}', 'ORBIT', 30),
    ('20000000-0000-4000-8000-000000000015', 'BADGE_STELLAR_LEGACY', 'Stellar Legacy', 'Forjar estrelas em 365 dias logicos distintos, nao necessariamente consecutivos.', 1000, '{"versao":1,"filtros":{"tipo":"FOCUS","status":"COMPLETED"},"campo_dia":"dia_local","consecutivos":false}', 'DIAS_ATIVOS', 365),
    ('20000000-0000-4000-8000-000000000016', 'BADGE_COOLING_NEBULA', 'Cooling Nebula', 'Concluir 25 descansos iniciados de 0 a 120 segundos apos o termino de um foco concluido do mesmo usuario.', 150, '{"versao":1,"filtros":{"tipo":"SHORT_BREAK","status":"COMPLETED"},"vinculo_foco":{"status":"COMPLETED","intervalo_maximo_segundos":120,"mesmo_usuario":true,"intervalo_inicio_inclusivo":0}}', 'CONTAGEM_DESCANSOS', 25),
    ('20000000-0000-4000-8000-000000000017', 'BADGE_CELESTIAL_SCRIBE', 'Celestial Scribe', 'Criar 10 anotacoes ligadas a sessoes concluidas.', 50, '{"versao":1,"sessao_vinculada":{"status":"COMPLETED"}}', 'CONTAGEM_ANOTACOES', 10),
    ('20000000-0000-4000-8000-000000000018', 'BADGE_ASTRAL_CHRONICLE', 'Astral Chronicle', 'Criar anotacoes em 30 dias logicos distintos, usando criado_em, nao data_anotacao.', 400, '{"versao":1,"campo_instante":"criado_em","fuso":"PERFIL_NA_AVALIACAO","virada_dia_local":"04:00","consecutivos":false}', 'DIAS_ANOTACOES', 30),
    ('20000000-0000-4000-8000-000000000019', 'BADGE_CELESTIAL_CLOCKWORK', 'Celestial Clockwork', 'Ter pelo menos 20 dias ativos em qualquer janela de 30 dias logicos consecutivos.', 400, '{"versao":1,"regra":"CELESTIAL_CLOCKWORK","janela_dias_logicos":30,"filtros":{"tipo":"FOCUS","status":"COMPLETED"}}', 'ESPECIAL', 20),
    ('20000000-0000-4000-8000-000000000020', 'BADGE_STABLE_FUSION', 'Stable Fusion', 'Somar 7 dias logicos com pelo menos 4 estrelas, cada estrela seguida de descanso concluido.', 400, '{"versao":1,"regra":"STABLE_FUSION","estrelas_minimas_por_dia":4,"todas_estrelas_com_descanso_concluido":true,"intervalo_maximo_descanso_segundos":120}', 'ESPECIAL', 7),
    ('20000000-0000-4000-8000-000000000021', 'BADGE_REIGNITION', 'Reignition', 'Forjar uma estrela apos pelo menos 14 dias logicos sem estrelas, com historico anterior.', 50, '{"versao":1,"regra":"REIGNITION","dias_logicos_sem_estrelas_minimos":14,"exige_historico_anterior":true}', 'ESPECIAL', 1),
    ('20000000-0000-4000-8000-000000000022', 'BADGE_PHOENIX_FLAME', 'Phoenix Flame', 'Alcancar orbit de 7 dias depois de perder uma orbit anterior de pelo menos 7 dias.', 150, '{"versao":1,"regra":"PHOENIX_FLAME","exige_orbit_anterior_perdida_minima":7}', 'ESPECIAL', 7);

INSERT INTO constelacao (
    id, codigo, nome, xp_recompensa, qtd_estrelas, qtd_insignias_requeridas, descricao
) VALUES
    ('30000000-0000-4000-8000-000000000001', 'THE_FIRST_FIRMAMENT', 'The First Firmament', 50, 10, 2, 'Desbloquear 2 das 3 insignias do conjunto e atingir o minimo de estrelas.'),
    ('30000000-0000-4000-8000-000000000002', 'THE_ETERNAL_COMPASS', 'The Eternal Compass', 250, 50, 2, 'Desbloquear 2 das 3 insignias do conjunto e atingir o minimo de estrelas.'),
    ('30000000-0000-4000-8000-000000000003', 'THE_NIGHT_SENTINEL', 'The Night Sentinel', 375, 75, 2, 'Desbloquear 2 das 3 insignias do conjunto e atingir o minimo de estrelas.'),
    ('30000000-0000-4000-8000-000000000004', 'THE_STARFORGE', 'The Starforge', 500, 100, 2, 'Desbloquear 2 das 3 insignias do conjunto e atingir o minimo de estrelas.'),
    ('30000000-0000-4000-8000-000000000005', 'THE_EMBERWING', 'The Emberwing', 500, 100, 2, 'Desbloquear 2 das 3 insignias do conjunto e atingir o minimo de estrelas.'),
    ('30000000-0000-4000-8000-000000000006', 'THE_CELESTIAL_ARCHIVE', 'The Celestial Archive', 750, 150, 2, 'Desbloquear 2 das 3 insignias do conjunto e atingir o minimo de estrelas.');

INSERT INTO insignia_constelacao (
    constelacao_id, insignia_id
) VALUES
    ('30000000-0000-4000-8000-000000000001', '20000000-0000-4000-8000-000000000001'),
    ('30000000-0000-4000-8000-000000000001', '20000000-0000-4000-8000-000000000012'),
    ('30000000-0000-4000-8000-000000000001', '20000000-0000-4000-8000-000000000016'),
    ('30000000-0000-4000-8000-000000000002', '20000000-0000-4000-8000-000000000012'),
    ('30000000-0000-4000-8000-000000000002', '20000000-0000-4000-8000-000000000013'),
    ('30000000-0000-4000-8000-000000000002', '20000000-0000-4000-8000-000000000014'),
    ('30000000-0000-4000-8000-000000000003', '20000000-0000-4000-8000-000000000007'),
    ('30000000-0000-4000-8000-000000000003', '20000000-0000-4000-8000-000000000006'),
    ('30000000-0000-4000-8000-000000000003', '20000000-0000-4000-8000-000000000010'),
    ('30000000-0000-4000-8000-000000000004', '20000000-0000-4000-8000-000000000001'),
    ('30000000-0000-4000-8000-000000000004', '20000000-0000-4000-8000-000000000003'),
    ('30000000-0000-4000-8000-000000000004', '20000000-0000-4000-8000-000000000004'),
    ('30000000-0000-4000-8000-000000000005', '20000000-0000-4000-8000-000000000021'),
    ('30000000-0000-4000-8000-000000000005', '20000000-0000-4000-8000-000000000022'),
    ('30000000-0000-4000-8000-000000000005', '20000000-0000-4000-8000-000000000003'),
    ('30000000-0000-4000-8000-000000000006', '20000000-0000-4000-8000-000000000017'),
    ('30000000-0000-4000-8000-000000000006', '20000000-0000-4000-8000-000000000018'),
    ('30000000-0000-4000-8000-000000000006', '20000000-0000-4000-8000-000000000015');
