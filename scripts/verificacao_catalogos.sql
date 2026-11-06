-- Executar manualmente no PostgreSQL, conectado a um banco descartavel.
-- Primeiro aplicar V1 a V6 com o Flyway da aplicacao.
-- Nao colocar este arquivo em src/main/resources/db/migration.
-- Nao ha asserts/runner: ler os resultados e registrar o observado.

-- 1. Esperado: 60, 12, 22, 6 e 18.
SELECT
    (SELECT count(*) FROM limite_nivel) AS niveis,
    (SELECT count(*) FROM patente) AS patentes,
    (SELECT count(*) FROM insignia) AS insignias,
    (SELECT count(*) FROM constelacao) AS constelacoes,
    (SELECT count(*) FROM insignia_constelacao) AS relacoes;

-- 2. Esperado: nenhuma linha. A formula deve valer para todos os niveis.
SELECT * FROM limite_nivel
WHERE nivel NOT BETWEEN 1 AND 60
   OR xp_necessario <> 50 * (nivel - 1) + 25 * (nivel - 1) * (nivel - 2) / 2;

-- Esperado: nivel 1 = 0; nivel 60 = 45725.
SELECT * FROM limite_nivel WHERE nivel IN (1, 2, 5, 10, 60) ORDER BY nivel;

-- 3. Esperado: patentes nos niveis 1, 6, 11, ..., 56, conforme D-6.
SELECT codigo, nome, nivel_minimo FROM patente ORDER BY nivel_minimo;

-- 4. Conferir as 22 linhas contra D-9 e o contrato no README-DEV.md.
SELECT codigo, nome, tipo_criterio, contagem_alvo, xp_recompensa, criterio_json
FROM insignia ORDER BY id;
-- Esperado: 6500 (soma do catalogo; nao e XP concedido a um usuario).
SELECT sum(xp_recompensa) AS xp_total_catalogo_insignias FROM insignia;
-- Esperado: nenhuma linha.
SELECT codigo FROM insignia
WHERE tipo_criterio IS NULL OR contagem_alvo <= 0
   OR xp_recompensa NOT IN (50, 150, 400, 1000)
   OR jsonb_typeof(criterio_json) <> 'object'
   OR criterio_json->>'versao' IS DISTINCT FROM '1';

-- 5. Esperado: 6 linhas, cada uma com 3 relacoes, 2 requeridas e recompensa = 5*estrelas.
SELECT c.codigo, c.qtd_estrelas, c.qtd_insignias_requeridas,
       c.xp_recompensa, count(ic.insignia_id) AS insignias_no_conjunto,
       string_agg(i.nome, ', ' ORDER BY i.nome) AS insignias
FROM constelacao c
JOIN insignia_constelacao ic ON ic.constelacao_id = c.id
JOIN insignia i ON i.id = ic.insignia_id
GROUP BY c.id ORDER BY c.id;
-- Esperado: nenhuma linha.
SELECT c.codigo FROM constelacao c
LEFT JOIN insignia_constelacao ic ON ic.constelacao_id = c.id
GROUP BY c.id
HAVING count(ic.insignia_id) <> 3
    OR c.qtd_insignias_requeridas <> 2
    OR c.xp_recompensa <> 5 * c.qtd_estrelas;
-- Esperado: 2425.
SELECT sum(xp_recompensa) AS xp_total_catalogo_constelacoes FROM constelacao;

-- 6. Constraints reais: conferir nomes e definicoes, sem presumir CHECKs ausentes.
SELECT conrelid::regclass AS tabela, conname, pg_get_constraintdef(oid) AS definicao
FROM pg_constraint
WHERE conrelid IN ('sessao'::regclass, 'sessao_evento'::regclass,
    'configuracoes_usuario'::regclass, 'patente'::regclass,
    'insignia_constelacao'::regclass, 'ganho_xp'::regclass,
    'amizade'::regclass, 'pedido_amizade'::regclass)
ORDER BY conrelid::regclass::text, conname;
SELECT indexname, indexdef FROM pg_indexes
WHERE tablename IN ('sessao', 'pedido_amizade', 'ganho_xp');

-- 7. Caminhos validos. Rodar de BEGIN ate ROLLBACK na mesma conexao.
BEGIN;
INSERT INTO usuario (id, email) VALUES
('f0000000-0000-4000-8000-000000000001', 'seed37-a@example.invalid'),
('f0000000-0000-4000-8000-000000000002', 'seed37-b@example.invalid');
INSERT INTO configuracoes_usuario (usuario_id)
VALUES ('f0000000-0000-4000-8000-000000000001');
-- Esperado: 1500, 300, true, true.
SELECT foco_em_segundos, pausa_em_segundos, iniciar_pausa_auto, som_habilitado
FROM configuracoes_usuario
WHERE usuario_id = 'f0000000-0000-4000-8000-000000000001';

INSERT INTO sessao (id, usuario_id, tipo, status, duracao_planejada,
    tempo_de_foco, tempo_de_pausa, dia_local, iniciado_em)
VALUES ('f1000000-0000-4000-8000-000000000001',
    'f0000000-0000-4000-8000-000000000001', 'FOCUS', 'RUNNING',
    1500, 0, 0, CURRENT_DATE, CURRENT_TIMESTAMP - interval '25 minutes');
INSERT INTO sessao_evento (id, sessao_id, payload, tipo_evento, origem)
VALUES
('f2000000-0000-4000-8000-000000000001',
 'f1000000-0000-4000-8000-000000000001', '{}', 'INICIADA', 'CLIENTE'),
('f2000000-0000-4000-8000-000000000002',
 'f1000000-0000-4000-8000-000000000001', '{}', 'CONCLUIDA', 'CLIENTE');
-- Esperado: 2 eventos na mesma sessao.
SELECT count(*) AS eventos FROM sessao_evento
WHERE sessao_id = 'f1000000-0000-4000-8000-000000000001';
UPDATE sessao SET status = 'COMPLETED', tempo_de_foco = 1500,
    terminado_em = CURRENT_TIMESTAMP
WHERE id = 'f1000000-0000-4000-8000-000000000001';
INSERT INTO sessao (id, usuario_id, tipo, status, duracao_planejada,
    tempo_de_foco, tempo_de_pausa, dia_local)
VALUES ('f1000000-0000-4000-8000-000000000002',
    'f0000000-0000-4000-8000-000000000001', 'FOCUS', 'RUNNING',
    1500, 0, 0, CURRENT_DATE);
-- Esperado: uma COMPLETED e uma RUNNING.
SELECT id, status FROM sessao
WHERE usuario_id = 'f0000000-0000-4000-8000-000000000001';

-- Ordem invertida de proposito: o trigger deve normalizar o par.
INSERT INTO pedido_amizade (id, remetente_id, destinatario_id)
VALUES ('f3000000-0000-4000-8000-000000000001',
    'f0000000-0000-4000-8000-000000000002',
    'f0000000-0000-4000-8000-000000000001');
UPDATE pedido_amizade SET status = 'ACEITO'
WHERE id = 'f3000000-0000-4000-8000-000000000001';
-- Esperado: uma amizade, ordenado=true.
SELECT usuario1_id, usuario2_id, usuario1_id < usuario2_id AS ordenado
FROM amizade WHERE usuario1_id = 'f0000000-0000-4000-8000-000000000001'
AND usuario2_id = 'f0000000-0000-4000-8000-000000000002';
-- Esperado: zero.
SELECT count(*) AS pedidos_restantes FROM pedido_amizade
WHERE id = 'f3000000-0000-4000-8000-000000000001';
ROLLBACK;

-- 8. Violacoes esperadas: executar CADA instrucao manualmente.
-- A ferramenta pode interromper ao receber o erro esperado.
-- Apos cada erro, executar ROLLBACK TO SAVEPOINT antes de continuar.
BEGIN;
INSERT INTO usuario (id, email)
VALUES ('f0000000-0000-4000-8000-000000000001', 'seed37-a@example.invalid');
INSERT INTO configuracoes_usuario (usuario_id)
VALUES ('f0000000-0000-4000-8000-000000000001');
INSERT INTO sessao (id, usuario_id, tipo, status, tempo_de_foco, tempo_de_pausa, dia_local)
VALUES ('f1000000-0000-4000-8000-000000000001',
    'f0000000-0000-4000-8000-000000000001', 'FOCUS', 'RUNNING', 0, 0, CURRENT_DATE);

SAVEPOINT antes_erro;
-- Esperado: unique_violation, idx_sessao_ativa.
INSERT INTO sessao (id, usuario_id, tipo, status, tempo_de_foco, tempo_de_pausa, dia_local)
VALUES ('f1000000-0000-4000-8000-000000000002',
    'f0000000-0000-4000-8000-000000000001', 'FOCUS', 'PAUSED', 0, 0, CURRENT_DATE);
ROLLBACK TO SAVEPOINT antes_erro;

-- Esperado: check_violation, foco_intervalo_check (901 nao e multiplo de 300).
UPDATE configuracoes_usuario SET foco_em_segundos = 901
WHERE usuario_id = 'f0000000-0000-4000-8000-000000000001';
ROLLBACK TO SAVEPOINT antes_erro;
-- Esperado: check_violation, pausa_intervalo_check.
UPDATE configuracoes_usuario SET pausa_em_segundos = 179
WHERE usuario_id = 'f0000000-0000-4000-8000-000000000001';
ROLLBACK TO SAVEPOINT antes_erro;

-- Esperado: foreign_key_violation, nivel 61 nao existe.
INSERT INTO patente (id, codigo, nome, nivel_minimo)
VALUES ('f4000000-0000-4000-8000-000000000001', 'INVALIDA_37', 'Invalida 37', 61);
ROLLBACK TO SAVEPOINT antes_erro;
-- Esperado: foreign_key_violation, insignia nao existe.
INSERT INTO insignia_constelacao (constelacao_id, insignia_id)
VALUES ('30000000-0000-4000-8000-000000000001', 'ffffffff-ffff-4fff-8fff-ffffffffffff');
ROLLBACK TO SAVEPOINT antes_erro;
-- Esperado: check_violation, origem_unica (nenhuma origem preenchida).
INSERT INTO ganho_xp (id, usuario_id, quantidade)
VALUES ('f5000000-0000-4000-8000-000000000001',
    'f0000000-0000-4000-8000-000000000001', 25);
ROLLBACK TO SAVEPOINT antes_erro;
ROLLBACK;

-- 9. Flyway: executar apos inicializacao da aplicacao.
-- Esperado: V1 a V6, success=true, sem falhas.
-- Se o Flyway usar outro schema, qualificar o nome abaixo.
SELECT installed_rank, version, description, script, success
FROM flyway_schema_history ORDER BY installed_rank;
