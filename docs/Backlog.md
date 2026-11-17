# Backlog — StarlightTimer

**Revisado em 17/11/2026.** Fontes: [Contexo](Contexo.md) para arquitetura, colaboração e estado do projeto; [10-domain-rules-decisions](10-domain-rules-decisions.md) para D-1 a D-11. O ER e o diagrama macro continuam superiores para schema e topologia. Esta lista organiza trabalho e aponta para as issues do GitHub; o estado da implementação usa a `dev` em `51bcf45`.

## Escopo e marcos

O MVP inclui autenticação, timer solo e descanso, histórico, anotações privadas, XP, níveis, 12 patentes, 22 insígnias, seis constelações e amizades. Ranking de amigos tem regra D-10 decidida, mas a funcionalidade é pós-MVP. Salas co-op, música, lembretes e cosméticos também são pós-MVP.

| Marco | Objetivo | Evidência de conclusão |
| --- | --- | --- |
| **M0 — Infra e regras de negócio (06/11)** | Infra, migrations e regras de negócio do domínio aos controllers | ✅ Fechado: 20/20 issues |
| **M1 — Autenticação, handlers e base do frontend (15/11)** | Auth, resumo do dia, cache, perfil de amigo, testes de fluxo, outbox + Kafka e base do frontend | 15 issues, #66–#80 |
| **M2 — Frontend: progressão (22/11)** | Telas de progressão, arte dos catálogos, nginx, convenções de API e worker de auditoria | 13 issues, #81–#93 |
| **M3 — Entrega (27/11)** | Nenhuma feature nova: bugs, dados de demonstração, README e ensaio | 10 issues, #94–#103 |
| **Pós-MVP** | Ranking, salas, música, lembretes, cosméticos | Decisões específicas e mudanças prévias nos diagramas |

A política de testes pós-M0 foi decidida em 17/11/2026 e está em [Contexo](Contexo.md) §10.1. A cobertura inicial e a adoção na CI seguem em BL-18.

## Decisões e bloqueios

- **D-1 a D-11:** resolvidos em [10-domain-rules-decisions](10-domain-rules-decisions.md). D-2 estabelece abandono da sessão ao ultrapassar 10 minutos acumulados de pausa, conforme confirmação do time em 05/11.
- **A-2** e **ABERTO-08** decididos na #66. O MVP usa e-mail e senha com acesso imediato. Falta incorporar ao ER as restrições de `PASSWORD` antes da migration #74. A #66 também inclui a revisão das notas e a publicação na `dev-docs`.
- **L-5:** fechar matriz de visibilidade campo a campo para perfil de amigo. Anotações nunca são visíveis a terceiros. Issue #67, bloqueia #75.
- **T-1:** escolher localização do frontend (#70). **Q-4:** estratégia decidida em [Contexo](Contexo.md) §10.1 (#68).
- **ABERTO-03:** confirmar tarefas colaterais de autenticação em worker ou API. Login e emissão de token permanecem síncronos na API.
- **ABERTO-10:** desenhar nome e estrutura do schema de auditoria no ER (#83, bloqueia #93).

## Fila de implementação

Os IDs **BL-01 a BL-18** preservam as referências de [Contexo](Contexo.md) §13. Cada entrega aponta para as issues do GitHub; branches saem de `dev` como `<tipo>/<número-da-issue>-<descrição>`. As relações de bloqueio entre issues estão registradas no próprio GitHub (*blocked by*). Estado da implementação conferido na `dev` em `51bcf45`. A cópia das notas no GitHub fica na `dev-docs`.

| ID | Entrega | Estado na `dev` | Issues |
| --- | --- | --- | --- |
| **BL-01** | Compose local para PostgreSQL e Redis, `.env.example` sem segredos | ✅ Concluído no M0 (compose com API, Postgres e Redis) | #13, #22 |
| **BL-02** | Versionar ER e diagrama macro, com links nas notas e no repositório | 🔜 M3 | #96 (depois de #66, #69 e #83) |
| **BL-03** | Datasource, Flyway, `ddl-auto=validate`, Redis e `open-in-view=false` | ⚠️ Concluído, exceto `spring.jpa.open-in-view=false`, ausente no `application.properties` | #13 · M3: #94 |
| **BL-04** | Esqueleto hexagonal, `Clock`, erros RFC 9457 | ✅ Concluído no M0 (`GlobalExceptionHandler` com `ProblemDetail`) | #17 |
| **BL-05** | Spring Security, JWT, rotas públicas/protegidas | 🔜 M1 | #66 (A-2, ABERTO-08) → #73 |
| **BL-06** | CI de compilação em PR com `./mvnw -DskipTests verify` | ✅ Concluído no M0 (`ci.yml`) | #22 |
| **BL-07** | Cadastro, login, refresh/logout, perfil e configurações | Cadastro local, perfil e configurações prontos; falta credencial e token | #25, #28 · M1: #74, #79 · M2: #89 |
| **BL-08** | Sessão: início, pausa, retomada, conclusão, abandono, histórico e job | ✅ Backend concluído no M0, com o job de sessões vencidas | #20, #26, #31 · M1: #80 · M2: #84 |
| **BL-09** | CRUD de notas de sessão e de dia | ✅ Backend concluído no M0 | #29, #33 · M2: #85 |
| **BL-10** | Ledger de XP, nível/patente, orbit, 22 insígnias e seis constelações | ✅ Backend concluído no M0; faltam resumo do dia, cache e arte dos catálogos | #21, #27, #32, #35, #37 · M1: #71, #72 · M2: #81, #86, #87, #88 |
| **BL-11** | Pedido, aceite/recusa/cancelamento, amizade, bloqueio, perfil de amigo | ✅ Backend concluído no M0, exceto o perfil de amigo | #19, #30, #34 · M1: #67 (L-5) → #75 · M2: #90 |
| **BL-12** | Outbox e Kafka: ER, migration, payload versionado, publicador, idempotência | 🔜 M1 | #69 → #77 |
| **BL-13** | Worker de auditoria e schema próprio | 🔜 M2 | #83 (ABERTO-10) → #93 |
| **BL-14** | nginx para API e build estático | 🔜 M2 | #92 |
| **BL-15** | Publicar a cópia de referência na `dev-docs` e alinhar os guias da branch de código | Cópia de referência no escopo da #66; guias da branch de código em M3 | #66, #100, #101 |
| **BL-16** | Decidir T-1 e criar frontend Vite + React + TypeScript | 🔜 M1 (base) e M2 (telas) | M1: #70, #78, #79, #80 · M2: #84–#91 · M3: #98 |
| **BL-17** | Convenções de API, paginação e formato de erro; avaliar OpenAPI | 🔜 M2 (`springdoc` já está na `dev`) | #82 |
| **BL-18** | Implementar cobertura prioritária de Q-4 e tornar a suíte obrigatória na CI | Política definida. Suíte e CI de testes pendentes | #68 → #76 · M3: #99 (regressão manual) |

### Issues por milestone

**M1 — Autenticação, handlers e base do frontend (até 15/11):** #66 A-2/ABERTO-08 · #67 L-5 · #68 política de testes · #69 outbox no ER · #70 scaffold do frontend · #71 resumo do dia · #72 `@Cacheable` nos catálogos · #73 Spring Security + JWT · #74 cadastro com senha, login, refresh e logout · #75 perfil de amigo · #76 testes de fluxo · #77 outbox + Kafka · #78 `deriveStage` e `<StellarTimer>` · #79 login e cadastro · #80 tela do Timer.

**M2 — Frontend: progressão (até 22/11):** #81 arte dos catálogos · #82 convenções de API · #83 schema de auditoria · #84 Star Log · #85 anotações · #86 Perfil · #87 Constelações · #88 recompensas · #89 Configurações · #90 amigos · #91 estados vazios e de erro (F-9) · #92 nginx · #93 worker de auditoria.

**M3 — Entrega (até 27/11):** #94 `open-in-view` ativo (bug) · #95 remover classes duplicadas sem uso · #96 versionar ER e diagrama macro · #97 dados de demonstração (perfil `demo`) · #98 acessibilidade (F-3, F-4) · #99 regressão manual e triagem de bugs · #100 sincronizar documentação · #101 README da raiz · #102 promover `dev` para `main` · #103 roteiro e ensaio da apresentação.

Caminho crítico: #66 → #73 → #74 → #79 → #80 → #88, e #70 → #78 → #80. Arte (#81) bloqueia Perfil e Constelações; estes, com o Star Log, bloqueiam os estados vazios (#91). No M3, a regressão (#99) espera todas as telas do M2, o nginx e o worker; a promoção para `main` (#102) espera regressão, documentação, README, diagramas, acessibilidade e os dois ajustes de código; o ensaio (#103) vem por último, com os dados de demonstração (#97).

## Critérios manuais prioritários

1. Foco de 15 a 90 minutos em passos de 5; descanso curto de 3 a 15 minutos; valores fora da faixa são recusados.
2. Aviso de conclusão com até 5 segundos restantes é aceito; com 6 segundos, responde `409` e informa o tempo restante. Abandono rende zero XP.
3. Sessão `RUNNING` vencida é concluída pelo job com `terminado_em` calculado; pausa acumulada acima de 600 segundos abandona sem XP.
4. `dia_local` é fixado no início usando virada às 04:00 no fuso do perfil; alteração posterior de fuso não reescreve o passado.
5. XP da sessão = minutos planejados; insígnia e constelação usam `xp_recompensa` do catálogo. Um evento não concede XP duplicado.
6. Aceite de pedido gera amizade pelo trigger e remove o pedido; a API consulta a amizade após o `UPDATE`.

Registrar comandos, resposta e consulta SQL observada na seção “Como foi testado” do PR. Esta seção é um roteiro manual, não uma suíte automatizada.
