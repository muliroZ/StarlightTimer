# 03 — Modelo de Domínio

**Revisado em 17/11/2026.** O ER é a fonte máxima para tabelas, colunas e constraints. [Contexo](Contexo.md) §6–8 define os módulos e as convenções de código; [10-domain-rules-decisions](10-domain-rules-decisions.md) fecha D-1 a D-11. Esta nota explica o modelo, sem substituir o diagrama.

## Núcleo do MVP

| Módulo | Entidades do ER | Regra central |
| --- | --- | --- |
| `identidade` | `usuario`, `identidade`, `perfil`, `configuracoes_usuario`, `refresh_token` | `perfil.fuso_horario` obrigatório; refresh token armazenado só como hash |
| `sessao` | `sessao`, `sessao_evento` | Relógio do servidor decide conclusão e pausa; `dia_local` é fixado no início |
| `anotacao` | `anotacao` | Nota de sessão ou de dia, privada por autorização por linha |
| `progressao` | `ganho_xp`, `limite_nivel`, `patente`, `insignia`, `insignia_usuario`, `constelacao`, `insignia_constelacao`, `constelacao_usuario` | Ledger de XP; níveis e patentes derivados; catálogos em dados |
| `social` | `pedido_amizade`, `amizade`, `lista_bloqueio` | Trigger cria amizade ao aceitar pedido e apaga o pedido |

**PKs:** UUID; tabelas de associação têm PK composta. **Nomes:** português, singular e `snake_case`. **Timestamps:** `TIMESTAMPTZ` em UTC. **Enum:** `VARCHAR` + `CHECK`, com valores literais nas migrations. `usuario` é a única tabela com soft delete. No MVP, A-2 define cadastro e login por e-mail e senha, com acesso imediato após o cadastro. ABERTO-08 define `PASSWORD` como provedor obrigatório; a credencial exige `senha_hash` e mantém `id_externo` nulo. O ER ainda precisa registrar o `CHECK` do provedor e a unicidade por usuário e provedor, preservando a unicidade por provedor e identificador externo. As tabelas de outbox e auditoria dependem de atualização prévia do ER.

## Sessão e estrela

`Sessao` nasce ao iniciar. `sessao_evento` registra transições; `ocorrido_em` é o carimbo autoritativo do servidor e `reportado_em` é informativo. A estrela é uma sessão de foco `COMPLETED` após 100% do tempo planejado, descontadas pausas, com tolerância de aviso antecipado de 5 segundos. Uma sessão abandonada dá zero XP. Descanso concluído dá zero XP e pode ser associado a um foco quando começa até dois minutos após o término.

Foco: 900–5400 segundos em múltiplos de 300. Descanso curto: 180–900 segundos. Apenas foco pode ser pausado. Ao ultrapassar 600 segundos de pausa acumulada, a sessão vira `ABANDONED`; uma sessão `RUNNING` vencida deve ser concluída pelo job com `terminado_em` calculado. O estágio visual é derivado do percentual, nunca persistido.

`dia_local` é o dia lógico do início da sessão. O dia vira às 04:00 no fuso IANA guardado no perfil; ele não muda após viagem ou atualização do fuso. Orbit exige ao menos uma estrela por dia lógico e é calculada sobre as sessões na leitura, sem tabela própria.

## Progressão

`ganho_xp` é append-only e cada linha tem exatamente uma origem: `origem_sessao`, `origem_insignia` ou `origem_constelacao`. Foco concluído rende 1 XP por minuto planejado; descanso e abandono rendem zero. Insígnias e constelações usam `xp_recompensa` do catálogo. `perfil.xp_total` é cache do ledger e muda na mesma transação da concessão.

A curva de nível e as 12 patentes estão em [10-domain-rules-decisions](10-domain-rules-decisions.md): 60 níveis; `xp_necessario` acumulado; patente derivada de `patente.nivel_minimo`. O MVP tem 22 insígnias e seis constelações. Uma constelação exige simultaneamente o mínimo de insígnias do conjunto e o mínimo de estrelas; seu progresso é derivado, e o desbloqueio concede XP uma única vez.

Critérios simples das insígnias podem ser avaliados por dados; quatro casos exigem código. Não construir avaliador genérico de JSON antes de três casos concretos. `insignia_usuario.contagem_progresso` guarda o melhor valor já alcançado e não regride.

## Social e privacidade

`pedido_amizade` guarda o pedido. Ao aceitar, o serviço valida destinatário, status e bloqueio e atualiza o status; o trigger insere `amizade` em ordem canônica e remove o pedido. `lista_bloqueio` é separada, pois o bloqueio deve sobreviver ao fim da amizade. L-5 ainda define a matriz exata dos campos visíveis ao amigo. Anotações nunca são compartilhadas.

## Limite de escopo

Salas, música, cosméticos, lembretes e ranking ficam fora do MVP. D-10 já define o futuro ranking: semanal, por minutos de foco concluídos, com virada segunda 00:00 em `America/Sao_Paulo`; não há snapshot no schema atual. D-12 (pausa longa) permanece posterior. Mudança de schema começa no ER, depois recebe migration Flyway nova.
