# 08 — Inventário de Entidades por Escopo

**Revisado em 17/11/2026.** O diagrama ER prevalece sobre esta lista. [Contexo](Contexo.md) §7 detalha as colunas e constraints; [10-domain-rules-decisions](10-domain-rules-decisions.md) define comportamento e seeds.

## MVP — 19 tabelas do ER

| Módulo | Tabelas | Observação |
| --- | --- | --- |
| Identidade (5) | `usuario`, `identidade`, `perfil`, `configuracoes_usuario`, `refresh_token` | Fuso IANA obrigatório; token só em hash |
| Sessão (2) | `sessao`, `sessao_evento` | `dia_local DATE NOT NULL`; evento com horário de servidor e horário reportado |
| Anotação (1) | `anotacao` | Privada; nota diária ou vinculada à sessão |
| Progressão (8) | `ganho_xp`, `limite_nivel`, `patente`, `insignia`, `insignia_usuario`, `constelacao`, `insignia_constelacao`, `constelacao_usuario` | Seis constelações no MVP |
| Social (3) | `pedido_amizade`, `amizade`, `lista_bloqueio` | Aceite de pedido via trigger |

**Total: 19 tabelas** (5 + 2 + 1 + 8 + 3). O rótulo antigo de 14 tabelas não se aplica. Tabelas de junção têm PK composta; demais PKs são UUID. `limite_nivel.nivel` é a exceção identificada no ER: PK INTEGER.

## Diferenças decisivas frente ao mapa candidato antigo

- O ER usa nomes em português, singular, com PK UUID. Não há `BIGINT` interno nem `public_id`.
- Sessões de descanso vivem em `sessao`; pausa longa é pós-MVP. `sessao_evento` registra início e transições.
- `ganho_xp` possui três FKs de origem mutuamente exclusivas. Não há origem de bônus por streak ou co-op.
- `patente.nivel_minimo` define a patente; não há `limite_nivel.patente_id`.
- `pedido_amizade` e `amizade` são tabelas distintas; o trigger de aceite insere a segunda e remove a primeira.
- `constelacao`, `insignia_constelacao` e `constelacao_usuario` integram o MVP.
- Não existem as âncoras `room_id`, `visibility` e `theme_id`. Futuras features alteram o ER antes de criar migration.
- Orbit é agregação de `sessao.dia_local`, não uma tabela.

## Catálogos iniciais

A migration `V6__seed_catalogos.sql` contém 60 níveis (nível 60: 45.725 XP), 12 patentes, 22 insígnias, seis constelações e 18 pares em `insignia_constelacao`. Critérios, nomes e recompensas estão em [10-domain-rules-decisions](10-domain-rules-decisions.md). Confira o ER e a V6 antes de propor alterações no catálogo.

## Pós-MVP e infraestrutura a modelar

Salas co-op, presença, música, cosméticos, lembretes e ranking são pós-MVP. D-10 define o ranking calculado na leitura; `LEADERBOARD_SNAPSHOT` não é necessária para a regra atual. Outbox e schema de auditoria foram decididos arquiteturalmente, mas **ainda precisam ser desenhados no ER** antes de migrations (BL-12/BL-13). O worker de autenticação continua em aberto (ABERTO-03).

## Bloqueios reais

D-1 a D-11 foram resolvidos. A-2 e ABERTO-08 foram decididos na #66. As restrições de `identidade` ainda precisam entrar no ER antes da migration de cadastro (#74). L-5 (visibilidade entre amigos) segue em aberto. Para futuras features, D-12 e R-1 a R-8 seguem abertos. [05-planning-checklist](05-planning-checklist.md) mantém os IDs.
