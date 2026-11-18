# 05 — Checklist de Planejamento

Registro de decisões resolvidas e em aberto. D-1 a D-11 foram fechadas em [10-domain-rules-decisions](10-domain-rules-decisions.md) em 05/11/2026. Cada item tem um ID para que possa ser referenciado a partir de outros documentos, tickets e atas de reunião.

**Bloqueante** = algo é impossível de construir até que isto seja respondido.
**Em breve** = necessário antes que a feature correspondente comece, mas não bloqueia hoje.
**Depois** = pode ser adiado sem custo.

---

## P — Produto & escopo

- [x] **P-1** — Resolvido: MVP do [01-product-overview](01-product-overview.md) inclui também seis constelações (D-11).
- [ ] **P-2** — *Em breve.* Validar as personas. Mesmo cinco conversas com usuários reais valem mais que quatro suposições.
- [ ] **P-3** — *Bloqueante.* Definir sucesso. "O app funciona" não é mensurável. Escolham dois ou três: retenção em 7 dias, mediana de ciclos por dia ativo, proporção de ciclos iniciados que se completam.
- [ ] **P-4** — *Bloqueante.* Plataformas-alvo. Só desktop? Responsivo até mobile? PWA instalável? O protótipo é desktop-first em três colunas e não sobrevive a uma viewport de 375px como está. Isso muda a estimativa de frontend substancialmente.
- [ ] **P-5** — *Em breve.* Existe modo offline? Um timer que exige conectividade para começar é um timer que falha no Wi-Fi de hotel.
- [ ] **P-6** — *Depois.* Monetização, se houver. Afeta se os cosméticos são conquistados, comprados, ou ambos.
- [x] **P-7** — *Em breve.* Escopo de lançamento: isso é público ou restrito a amigos e colegas de turma? Determina o quanto as questões de abuso, moderação e escala realmente importam.

## D — Regras de domínio

D-1 a D-11 estão decididos em [10-domain-rules-decisions](10-domain-rules-decisions.md); o texto integral dessa nota prevalece para números, critérios e catálogos.

- [x] **D-1** — Ciclo de foco concluído exige 100% do tempo medido pelo servidor; abandono rende zero XP.
- [x] **D-2** — Apenas foco pode ser pausado; acima de 10 minutos acumulados, a sessão vira `ABANDONED`. Pausa não soma ao *in orbit*.
- [x] **D-3** — Aviso até 5 segundos antes é aceito; acima disso, `409` com segundos restantes.
- [x] **D-4** — Estágios proporcionais à duração, derivados no cliente.
- [x] **D-5** — 1 XP por minuto de foco planejado concluído; insígnias e constelações dão recompensa do catálogo; sem bônus.
- [x] **D-6** — 60 níveis, 12 patentes, curva e limiares definidos em [10-domain-rules-decisions](10-domain-rules-decisions.md).
- [x] **D-7** — Orbit exige uma estrela por dia lógico; virada às 04:00 no fuso do perfil; dia fixado no início da sessão.
- [x] **D-8** — Anotações privadas por autorização de linha, não compartilhadas e sem criptografia contra operadores no MVP.
- [x] **D-9** — 22 insígnias do MVP com critérios e recompensas definidos.
- [x] **D-10** — Ranking de amigos pós-MVP: semanal, minutos concluídos, segunda 00:00 em `America/Sao_Paulo`.
- [x] **D-11** — Seis constelações no MVP, cada uma exigindo mínimo de insígnias e estrelas.
- [ ] **D-12** — *Depois.* Pausas longas e presets ainda dependem de decisão.

## R — Salas co-op

A feature mais mal especificada e a mais cara. Não comecem até que todas estas estejam respondidas.

- [ ] **R-1** — *Bloqueante (para a feature).* Timer compartilhado ou timers paralelos? Todo mundo roda um ciclo sincronizado, ou cada membro roda o seu enquanto compartilha presença? São features inteiramente diferentes, com custos inteiramente diferentes.
- [ ] **R-2** — *Bloqueante (para a feature).* Quem pode controlar o timer? Só o host, qualquer pessoa, ou a maioria? O que acontece em caso de discordância?
- [ ] **R-3** — *Bloqueante (para a feature).* Comportamento na desconexão do host. A sala pausa, transfere o host, continua sozinha, ou fecha?
- [ ] **R-4** — *Bloqueante (para a feature).* Entrada tardia. Alguém pode entrar no meio do ciclo? A pessoa entra na posição atual, espera o próximo ciclo, ou inicia o seu próprio?
- [ ] **R-5** — *Bloqueante (para a feature).* Ciclos co-op concedem XP igual aos solo? Se concedem mais, viram a estratégia ótima de grind e o uso solo definha. Se concedem menos, ninguém usa as salas.
- [ ] **R-6** — *Em breve.* Capacidade da sala, mecanismo de convite (link, convite direto, amigos-podem-só-entrar) e tempo de vida (efêmera vs persistente).
- [ ] **R-7** — *Em breve.* Existe chat? Se sim, isso é escopo de moderação, persistência e denúncia de abuso que hoje não está contabilizado em lugar nenhum.
- [ ] **R-8** — *Em breve.* Granularidade da presença — "Ana está focando" é visível para amigos fora de uma sala? Isso é tanto uma decisão de privacidade quanto uma feature.

## A — Autenticação & segurança

- [x] **A-1** — *Bloqueante.* Estratégia de token: cookie de sessão, JWT em memória com refresh, ou JWT em cookie httpOnly. Afeta a SPA, o handshake do WebSocket e a postura contra CSRF.
- [x] **A-2** — No MVP, cadastro e login usam apenas e-mail e senha. O login fica disponível após o cadastro, sem confirmação de e-mail. [#66](https://github.com/muliroZ/StarlightTimer/issues/66)
  - **ABERTO-08** — `provedor` obrigatório com `CHECK (provedor IN ('PASSWORD'))`. Identidades `PASSWORD` exigem `senha_hash` e têm `id_externo` nulo. Adotar `UNIQUE (usuario_id, provedor)` e manter `UNIQUE (provedor, id_externo)` para futuros logins sociais. Registro no ER pendente. Ver [14 — Decisões: resolvidas e em aberto](Contexo.md#14--decis%C3%B5es-resolvidas-e-em-aberto).
- [ ] **A-3** — *Bloqueante.* Autenticação do WebSocket. Tokens em query string acabam em logs de proxy; decidam o mecanismo de handshake explicitamente.
- [ ] **A-4** — *Em breve.* Fluxo de redefinição de senha, e o que ele faz com qualquer criptografia derivada da chave do usuário, caso **D-8** caia na leitura criptografada.
- [ ] **A-5** — *Em breve.* Rate limiting, especialmente nos endpoints de conclusão de sessão (a torneira de XP) e nos pedidos de amizade (o vetor de spam).
- [x] **A-6** — *Em breve.* Postura anti-cheat. A validação no servidor está em **AD-2**, mas decidam quanto esforço se justifica. Ranking apenas entre amigos torna isso de baixo risco; digam isso em voz alta para que ninguém faça over-engineering.
- [ ] **A-7** — *Depois.* Denúncia de abuso e bloqueio, se o app for público (**P-7**).

## L — Jurídico & privacidade

- [ ] **L-1** — *Bloqueante, se música entrar.* **Licenciamento de música.** Vocês não podem legalmente transmitir música arbitrária a partir dos próprios servidores. Opções realistas: (a) integrar um provedor como o Spotify, o que exige a conta premium do próprio usuário e restringe o controle de reprodução; (b) licenciar ou encomendar loops ambientes originais; (c) áudio royalty-free / com licença CC e atribuição; (d) deixar os usuários trazerem a própria música e simplesmente não lidar com áudio. Esta é uma questão jurídica, não técnica, e é a feature com maior probabilidade de ser cortada tardiamente por motivos que ninguém antecipou.
- [x] **L-2** — *Bloqueante.* Qual regime de privacidade se aplica (LGPD, GDPR, ambos)? Determina obrigações de consentimento, exportação e exclusão.
- [ ] **L-3** — *Bloqueante.* Semântica da exclusão de conta. Hard delete ou anonimização? O que acontece com as linhas de um usuário excluído no histórico de ranking de um amigo, ou com sua participação em uma sala compartilhada?
- [ ] **L-4** — *Em breve.* Retenção de dados. As sessões persistem para sempre? E as anotações?
- [x] **L-5** — *Resolvido.* Matriz exata de visibilidade entre amigos. A regra é aplicada obrigatoriamente na camada de serviço (`social` e `progressao`), nunca apenas na interface.

  | Categoria | Campo / Dado | Termo (UI / Schema) | Visível para Amigo | Nunca Visível (Privado) | Observação |
    | :--- | :--- | :--- | :---: | :---: | :--- |
  | **Identidade** | ID, nome exibido, avatar e bio | `usuario_id`, `nome_exibido`, `avatar`, `bio` | Sim | — | Também visíveis na busca pública de usuários (exceto quando há bloqueio) |
  | **Identidade** | E-mail e credenciais | `email`, `senha_hash`, `refresh_token` | — | **Nunca** | Dados estritamente privados da conta |
  | **Identidade** | Fuso horário e configurações | `fuso_horario`, `configuracoes_usuario` | — | **Nunca** | Preferências individuais de timer e som |
  | **Progressão** | Estrelas forjadas | *Stars forged* (`sessao` `COMPLETED`) | Sim | — | Total vitalício agregado de ciclos de foco concluídos |
  | **Progressão** | Órbita (sequência) | *Orbit* (streak sobre `dia_local`) | Sim | — | Contagem atual de dias lógicos consecutivos ativos |
  | **Progressão** | Tempo em órbita | *In orbit* (soma de `tempo_de_foco`) | Sim | — | Total vitalício acumulado de foco em minutos/segundos |
  | **Progressão** | XP total, nível e patente | `xp_total`, `limite_nivel`, `patente` | Sim | — | Nível e patente atuais derivados do ledger `ganho_xp` |
  | **Progressão** | Insígnias desbloqueadas | `insignia_usuario` (`desbloqueado_em IS NOT NULL`) | Sim | — | Progresso parcial de insígnias ainda bloqueadas (`contagem_progresso`) fica privado ao próprio usuário |
  | **Progressão** | Constelações desbloqueadas | `constelacao_usuario` | Sim | — | Inclui estado visual de constelação perfeita derivado das 3 insígnias |
  | **Sessão** | Histórico individual e ciclos | *Star Log* (`sessao`, `sessao_evento`) | — | **Nunca** | Apenas os totais agregados acima são expostos |
  | **Sessão** | Atividade atual em tempo real | Presença / ciclo em execução | — | **Nunca (no MVP)** | Depende de presença e salas co-op (**R-8**), fora do escopo do MVP |
  | **Anotações** | Anotações de sessão ou de dia | `anotacao` (**D-8**) | — | **Nunca** | Privadas por autorização por linha; jamais compartilhadas com amigos ou terceiros |

  **Comportamento para amizade desfeita e bloqueio:**
  - **Amizade desfeita:** A linha em `amizade` é removida. O antigo amigo perde imediatamente o acesso aos dados de progressão e gamificação; qualquer consulta ao endpoint de perfil de amigo passa a retornar **404 Not Found** (`RecursoNaoEncontradoException`, mesmo padrão de ocultação de **D-8**). O usuário volta a aparecer apenas com os dados básicos na busca e pode receber um novo pedido.
  - **Bloqueio (`lista_bloqueio`):** Ao bloquear um usuário, qualquer amizade ativa ou pedido pendente entre ambos é removido, mantendo-se o registro unidirecional em `lista_bloqueio`. Enquanto existir bloqueio em qualquer direção (A bloqueou B ou B bloqueou A), ambos ficam invisíveis entre si nas buscas, e tentativas de acessar perfil, enviar pedido de amizade ou interagir retornam **404 Not Found** (nunca 403, para não confirmar o bloqueio).
- [x] **L-6** — *Em breve.* Termos de uso e política de privacidade. Necessários antes de qualquer lançamento público.
- [x] **L-7** — *Depois.* Banner de cookies/consentimento, se houver analytics.

## N — Notificações

- [ ] **N-1** — *Em breve.* Canais: só in-app, Web Push, e-mail, ou uma combinação.
- [ ] **N-2** — *Em breve.* Se for Web Push: service worker, chaves VAPID e o fluxo de pedido de permissão. Nunca peçam no primeiro carregamento da página — peçam quando o usuário habilitar um lembrete.
- [ ] **N-3** — *Em breve.* Tipos de lembrete e seus gatilhos. "Orbit reminders — me cutuque se eu me dispersar por 10 min" aparece no protótipo, o que implica detecção de dispersão: como a dispersão é detectada no servidor quando o usuário simplesmente fechou a aba?
- [ ] **N-4** — *Em breve.* Horário de silêncio e limites de frequência. Um app de produtividade que importuna às 2h da manhã é desinstalado.
- [ ] **N-5** — *Depois.* Granularidade das preferências de notificação — por tipo, ou um único interruptor global?

## T — Técnico & infraestrutura

- [ ] **T-1** — *Bloqueante.* Estratégia de repositório: monorepo ou repositórios separados de frontend/backend. Afeta a CI, o versionamento e como mudanças de contrato são coordenadas.
- [x] **T-2** — *Bloqueante.* Versão do Java e versão do Spring Boot. Fixem ambas.
- [x] **T-3** — *Bloqueante.* Ferramenta de migration: Flyway ou Liquibase. O doc 02 assume Flyway; qualquer uma serve, mas escolham uma e configurem `ddl-auto: validate`.
- [ ] **T-4** — *Bloqueante.* Ambiente de desenvolvimento local. Docker Compose com Postgres é o padrão de menor atrito. "Funciona na minha máquina" custa mais dias do que montar isso.
- [ ] **T-5** — *Bloqueante.* Ambientes: quantos, e qual o caminho de promoção entre eles?
- [x] **T-6** — *Em breve.* Hospedagem para app, banco e frontend estático. Restrições de orçamento vão pesar mais aqui do que a arquitetura.
- [ ] **T-7** — *Em breve.* Pipeline de CI: a verificação de build em PRs para `dev` e `main` já existe. A obrigatoriedade da suíte automatizada depende da execução reproduzível na CI (Q-4, #76).
- [ ] **T-8** — *Em breve.* Gestão de segredos. Nada de `application.properties` no git.
- [ ] **T-9** — *Em breve.* Convenções de API: estrutura de URL, versionamento, paginação, formato de erro, formato de data. Escrevam um documento de convenções de uma página *antes* do décimo endpoint, não depois.
- [ ] **T-10** — *Em breve.* Geração de OpenAPI, e se os tipos do frontend são gerados a partir dele. Tipos gerados eliminam uma classe inteira de bug de integração por cerca de uma tarde de configuração.
- [ ] **T-11** — *Em breve.* Observabilidade: logging estruturado, rastreamento de erros, monitoramento de disponibilidade.
- [ ] **T-12** — *Em breve.* Backup e restore do Postgres — e confirmem que o restore foi de fato testado.
- [x] **T-13** — Resolvido: Redis faz parte da topologia como cache-aside; a aplicação funciona quando ele falha. Ver [Contexo](Contexo.md) §5.
- [ ] **T-14** — *Depois.* Metas não funcionais: usuários concorrentes esperados, latência aceitável, meta de disponibilidade. Números provisórios servem; o valor está em ter algo contra o que testar.

## F — Frontend

- [ ] **F-1** — *Bloqueante.* Ferramenta de build e roteador. Vite + React Router é o padrão convencional.
- [ ] **F-2** — *Bloqueante.* Divisão do gerenciamento de estado: o que cuida do estado de servidor, o que cuida do estado de cliente. O doc 04 explica por que devem ser ferramentas diferentes.
- [ ] **F-3** — *Bloqueante.* **Auditoria de contraste.** Meçam o texto de baixa opacidade contra o fundo escuro. Várias combinações entre `.45` e `.62` de opacidade provavelmente estão abaixo do WCAG AA. Estabeleçam uma opacidade mínima para elementos que carregam texto e apliquem de forma consistente.
- [ ] **F-4** — *Bloqueante.* Tratamento de `prefers-reduced-motion` para as quatro animações infinitas. Inegociável para uma tela que o usuário encara por 25 minutos.
- [ ] **F-5** — *Bloqueante.* Estratégia responsiva conforme **P-4**. O layout de três colunas do timer precisa de um comportamento de colapso definido.
- [ ] **F-6** — *Em breve.* Abordagem de estilização: CSS Modules, Tailwind ou CSS-in-JS. O protótipo usa estilos inline, o que é um artefato de protótipo, não uma recomendação — mas mantenham a camada de tokens como CSS custom properties de qualquer forma, para que tematização futura (cosméticos pós-MVP) continue barata.
- [ ] **F-7** — *Em breve.* Biblioteca de componentes: construir primitivos ou adotar uma biblioteca headless. A identidade visual é distintiva o bastante para que bibliotecas de componentes pesadas briguem com vocês.
- [ ] **F-8** — *Em breve.* Projetar as oito telas faltantes listadas no doc 04.
- [ ] **F-9** — *Em breve.* **Estados vazios.** Especificamente o perfil do primeiro dia. Esta é a primeira impressão de um produto motivacional e hoje ela mostra zeros.
- [ ] **F-10** — *Em breve.* Acessibilidade por teclado e indicadores de foco. O protótipo tem estilos de hover, mas nenhum anel de foco visível.
- [ ] **F-11** — *Em breve.* Comportamento do título da aba e do favicon durante um ciclo em execução — mostrar o tempo restante no título da aba é uma feature pequena com valor desproporcional para um timer em segundo plano.
- [ ] **F-12** — *Depois.* Internacionalização. Se o time é brasileiro, decidam agora se o português entra no lançamento; adaptar i18n depois é significativamente mais caro do que começar com ela.
- [ ] **F-13** — *Depois.* Upload de avatares — armazenamento, redimensionamento, moderação. O protótipo usa iniciais geradas, que é uma resposta permanente perfeitamente boa.

## Q — Qualidade & processo

- [ ] **Q-1** — *Bloqueante.* Definition of done. Definir os critérios gerais de conclusão. A política de testes já está em Q-4 e [Contexo](Contexo.md) §10.1.
- [x] **Q-2** — *Bloqueante.* Modelo de branches e regras de code review — revisores obrigatórios, e se o autor pode dar merge.
- [x] **Q-3** — *Bloqueante.* Rastreador de issues e como um ticket se move pelo board.
- [x] **Q-4** — *Decidido em 17/11/2026.* Assistentes de IA podem executar os testes relevantes e escrever ou alterar testes conforme [Contexo](Contexo.md) §10.1. Priorizar testes unitários de domínio e testes de fluxo com PostgreSQL via Testcontainers, com Redis nos cenários de cache. A execução da suíte passa a ser obrigatória na CI quando for reproduzível. A implementação segue em BL-18 (#68 → #76).
- [ ] **Q-5** — *Em breve.* Propriedade. Quem é dono do backend, do frontend, do schema e das decisões de design? Times pequenos costumam pular isso e depois descobrem que o schema tem três autores.
- [ ] **Q-6** — *Em breve.* Manutenção da documentação — estes documentos ficam obsoletos em um mês, a menos que atualizá-los faça parte de **Q-1**.
- [ ] **Q-7** — *Depois.* Orçamento de performance e testes de carga.

---

## Sequenciamento sugerido

A ordem de dependências que desbloqueia mais trabalho mais rápido:

**1. Fundação do produto** — P-1 e P-7 resolvidos; decidir P-3 e P-4 antes de fechar estimativas de interface.

**2. Regras de domínio** — D-1 a D-11 resolvidos em [10-domain-rules-decisions](10-domain-rules-decisions.md). Aplicar as decisões ao ER e às migrations antes de implementar.

**3. ER e migrations** — o ER é fonte máxima. Atualizar o diagrama antes de cada nova migration; o doc 03 apenas explica o modelo.

**4. Fundação técnica** — T-2, T-3, A-1, A-2, Q-2 e Q-3 resolvidos; priorizar T-1, T-4, T-5, F-1, F-2 e Q-1.

**5. Acessibilidade e lacunas de design** — F-3, F-4, F-5, F-9
Deliberadamente cedo. Os quatro ficam exponencialmente mais caros depois que a biblioteca de componentes existir.

**6. Jurídico** — L-1, L-2, L-3
O L-1 em particular deve ser resolvido antes que alguém estime a feature de música, porque a resposta pode eliminar a feature.

**7. Salas co-op** — R-1 a R-8, apenas quando a feature for de fato agendada.

## Itens de maior risco

Se vocês só forem atrás de quatro coisas esta semana:

| Item | Por que é perigoso |
|---|---|
| **L-5** Visibilidade entre amigos | Falta a matriz de campos permitidos na API; anotações ficam sempre privadas |
| **L-1** Licenciamento de música | Bloqueio jurídico que pode eliminar uma feature planejada depois de ela ter sido desenhada e estimada |
| **R-1** Modelo de sala | "Timer compartilhado" e "timers paralelos com presença" diferem em uma ordem de grandeza de custo, e hoje ambos têm o mesmo nome |
| **F-3/F-4** Acessibilidade | A estética (baixa opacidade, movimento constante) está em tensão direta com acessibilidade, e a correção é muito mais barata antes do design system ser codificado do que depois |
