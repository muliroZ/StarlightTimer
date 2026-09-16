# 05 — Checklist de Planejamento

Toda decisão que ainda está em aberto. Cada item tem um ID para que possa ser referenciado a partir de outros documentos, tickets e atas de reunião.

**Bloqueante** = algo é impossível de construir até que isto seja respondido.
**Em breve** = necessário antes que a feature correspondente comece, mas não bloqueia hoje.
**Depois** = pode ser adiado sem custo.

---

## P — Produto & escopo

- [ ] **P-1** — *Bloqueante.* Confirmar ou rejeitar o limite de MVP proposto no doc 01. Tudo daqui para frente depende dessa linha.
- [ ] **P-2** — *Em breve.* Validar as personas. Mesmo cinco conversas com usuários reais valem mais que quatro suposições.
- [ ] **P-3** — *Bloqueante.* Definir sucesso. "O app funciona" não é mensurável. Escolham dois ou três: retenção em 7 dias, mediana de ciclos por dia ativo, proporção de ciclos iniciados que se completam.
- [ ] **P-4** — *Bloqueante.* Plataformas-alvo. Só desktop? Responsivo até mobile? PWA instalável? O protótipo é desktop-first em três colunas e não sobrevive a uma viewport de 375px como está. Isso muda a estimativa de frontend substancialmente.
- [ ] **P-5** — *Em breve.* Existe modo offline? Um timer que exige conectividade para começar é um timer que falha no Wi-Fi de hotel.
- [ ] **P-6** — *Depois.* Monetização, se houver. Afeta se os cosméticos são conquistados, comprados, ou ambos.
- [ ] **P-7** — *Em breve.* Escopo de lançamento: isso é público ou restrito a amigos e colegas de turma? Determina o quanto as questões de abuso, moderação e escala realmente importam.

## D — Regras de domínio

Estas são as regras que ninguém escreveu ainda, e cada uma delas é necessária antes que o código correspondente possa ser escrito.

- [ ] **D-1** — *Bloqueante.* **O que conta como um ciclo concluído?** A duração completa precisa transcorrer? Existe um limiar de tolerância (ex.: 90% conta)? Um ciclo encerrado antes do tempo chega a conceder XP parcial?
- [ ] **D-2** — *Bloqueante.* **Semântica da pausa.** Um ciclo de foco pode ser pausado? Existe duração máxima de pausa antes de ser auto-abandonado? O tempo de pausa conta para as horas "in orbit"? (Um ciclo pausado por duas horas e depois concluído é atualmente uma estrela válida, o que provavelmente está errado.)
- [ ] **D-3** — *Bloqueante.* **Tolerância de relógio.** Quantos segundos de desvio o servidor aceita entre a duração esperada e a real antes de rejeitar uma conclusão? Precisa de um número, porque navegadores limitam abas em segundo plano e o desvio é real.
- [ ] **D-4** — *Em breve.* **Escala dos estágios.** Com um ciclo de 50 minutos, o arco da estrela se estica proporcionalmente (Main Sequence em 40–68% = minutos 20–34), ou as fronteiras dos estágios são fixas em minutos absolutos? Proporcional é o comportamento atual do protótipo.
- [ ] **D-5** — *Bloqueante.* **Fórmula de XP.** Fixa por ciclo? Escalada pela duração? Bônus por streak, por co-op, por concluir sem pausar? Escrevam a fórmula antes de escrever o ledger.
- [ ] **D-6** — *Bloqueante.* **Curva de níveis e limiares de rank.** Quanto XP por nível, e isso escala? Quais ranks nomeados existem e em quais níveis? O protótipo referencia "Stellar Cartographer" e "Quasar" sem nenhuma lista de tiers definida.
- [ ] **D-7** — *Bloqueante.* **Definição de streak.** O que quebra uma orbit — um dia com zero ciclos concluídos, ou um dia com zero ciclos *iniciados*? Avaliado no fuso de quem? E alguém que trabalha depois da meia-noite, que é exatamente a persona do badge "Night Watch"? Lógica de streak ingênua quanto a fuso horário é uma das fontes mais comuns de bugs visíveis ao usuário nesta categoria de app.
- [ ] **D-8** — *Bloqueante.* **O que "privada" significa para anotações.** Não-compartilhada-com-amigos, ou criptografada-contra-operadores? Veja o doc 03. A resposta muda o schema e o custo.
- [ ] **D-9** — *Em breve.* **Catálogo de badges.** Lista completa com critérios exatos. O protótipo mostra seis de um total declarado de quatorze; os outros oito estão indefinidos, e os seis mostrados não têm critérios escritos.
- [ ] **D-10** — *Em breve.* **Mecânica de ranking.** Vitalício ou periódico? Se periódico, qual período e quando reseta? Qual o critério de desempate? Um amigo que entra hoje começa do zero contra um amigo com seis meses de histórico — e, se sim, isso é desmotivante o suficiente para importar?
- [ ] **D-11** — *Em breve.* **Constelações.** O que é uma, concretamente? Um grupo temático de badges, um grupo de metas, um arranjo visual de estrelas conquistadas? O termo aparece na UI sem nenhuma definição por trás.
- [ ] **D-12** — *Depois.* **Pausas longas.** A regra clássica de "pausa longa a cada 4 ciclos" se aplica? É configurável?

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

- [ ] **A-1** — *Bloqueante.* Estratégia de token: cookie de sessão, JWT em memória com refresh, ou JWT em cookie httpOnly. Afeta a SPA, o handshake do WebSocket e a postura contra CSRF.
- [ ] **A-2** — *Bloqueante.* Fluxo de cadastro: só e-mail + senha, ou login social também? A verificação de e-mail é obrigatória antes do uso?
- [ ] **A-3** — *Bloqueante.* Autenticação do WebSocket. Tokens em query string acabam em logs de proxy; decidam o mecanismo de handshake explicitamente.
- [ ] **A-4** — *Em breve.* Fluxo de redefinição de senha, e o que ele faz com qualquer criptografia derivada da chave do usuário, caso **D-8** caia na leitura criptografada.
- [ ] **A-5** — *Em breve.* Rate limiting, especialmente nos endpoints de conclusão de sessão (a torneira de XP) e nos pedidos de amizade (o vetor de spam).
- [ ] **A-6** — *Em breve.* Postura anti-cheat. A validação no servidor está em **AD-2**, mas decidam quanto esforço se justifica. Ranking apenas entre amigos torna isso de baixo risco; digam isso em voz alta para que ninguém faça over-engineering.
- [ ] **A-7** — *Depois.* Denúncia de abuso e bloqueio, se o app for público (**P-7**).

## L — Jurídico & privacidade

- [ ] **L-1** — *Bloqueante, se música entrar.* **Licenciamento de música.** Vocês não podem legalmente transmitir música arbitrária a partir dos próprios servidores. Opções realistas: (a) integrar um provedor como o Spotify, o que exige a conta premium do próprio usuário e restringe o controle de reprodução; (b) licenciar ou encomendar loops ambientes originais; (c) áudio royalty-free / com licença CC e atribuição; (d) deixar os usuários trazerem a própria música e simplesmente não lidar com áudio. Esta é uma questão jurídica, não técnica, e é a feature com maior probabilidade de ser cortada tardiamente por motivos que ninguém antecipou.
- [ ] **L-2** — *Bloqueante.* Qual regime de privacidade se aplica (LGPD, GDPR, ambos)? Determina obrigações de consentimento, exportação e exclusão.
- [ ] **L-3** — *Bloqueante.* Semântica da exclusão de conta. Hard delete ou anonimização? O que acontece com as linhas de um usuário excluído no histórico de ranking de um amigo, ou com sua participação em uma sala compartilhada?
- [ ] **L-4** — *Em breve.* Retenção de dados. As sessões persistem para sempre? E as anotações?
- [ ] **L-5** — *Em breve.* Matriz exata de visibilidade entre amigos. Escrevam, campo a campo, o que um amigo pode ver: stars forged, streak, atividade atual, badges, títulos de sessão? Anotações precisam aparecer na coluna "nunca".
- [ ] **L-6** — *Em breve.* Termos de uso e política de privacidade. Necessários antes de qualquer lançamento público.
- [ ] **L-7** — *Depois.* Banner de cookies/consentimento, se houver analytics.

## N — Notificações

- [ ] **N-1** — *Em breve.* Canais: só in-app, Web Push, e-mail, ou uma combinação.
- [ ] **N-2** — *Em breve.* Se for Web Push: service worker, chaves VAPID e o fluxo de pedido de permissão. Nunca peçam no primeiro carregamento da página — peçam quando o usuário habilitar um lembrete.
- [ ] **N-3** — *Em breve.* Tipos de lembrete e seus gatilhos. "Orbit reminders — me cutuque se eu me dispersar por 10 min" aparece no protótipo, o que implica detecção de dispersão: como a dispersão é detectada no servidor quando o usuário simplesmente fechou a aba?
- [ ] **N-4** — *Em breve.* Horário de silêncio e limites de frequência. Um app de produtividade que importuna às 2h da manhã é desinstalado.
- [ ] **N-5** — *Depois.* Granularidade das preferências de notificação — por tipo, ou um único interruptor global?

## T — Técnico & infraestrutura

- [ ] **T-1** — *Bloqueante.* Estratégia de repositório: monorepo ou repositórios separados de frontend/backend. Afeta a CI, o versionamento e como mudanças de contrato são coordenadas.
- [ ] **T-2** — *Bloqueante.* Versão do Java e versão do Spring Boot. Fixem ambas.
- [ ] **T-3** — *Bloqueante.* Ferramenta de migration: Flyway ou Liquibase. O doc 02 assume Flyway; qualquer uma serve, mas escolham uma e configurem `ddl-auto: validate`.
- [ ] **T-4** — *Bloqueante.* Ambiente de desenvolvimento local. Docker Compose com Postgres é o padrão de menor atrito. "Funciona na minha máquina" custa mais dias do que montar isso.
- [ ] **T-5** — *Bloqueante.* Ambientes: quantos, e qual o caminho de promoção entre eles?
- [ ] **T-6** — *Em breve.* Hospedagem para app, banco e frontend estático. Restrições de orçamento vão pesar mais aqui do que a arquitetura.
- [ ] **T-7** — *Em breve.* Pipeline de CI: o que roda em um PR e o que bloqueia um merge.
- [ ] **T-8** — *Em breve.* Gestão de segredos. Nada de `application.properties` no git.
- [ ] **T-9** — *Em breve.* Convenções de API: estrutura de URL, versionamento, paginação, formato de erro, formato de data. Escrevam um documento de convenções de uma página *antes* do décimo endpoint, não depois.
- [ ] **T-10** — *Em breve.* Geração de OpenAPI, e se os tipos do frontend são gerados a partir dele. Tipos gerados eliminam uma classe inteira de bug de integração por cerca de uma tarde de configuração.
- [ ] **T-11** — *Em breve.* Observabilidade: logging estruturado, rastreamento de erros, monitoramento de disponibilidade.
- [ ] **T-12** — *Em breve.* Backup e restore do Postgres — e confirmem que o restore foi de fato testado.
- [ ] **T-13** — *Depois.* Se o Redis é necessário. Conforme o doc 02, adiem até que o deploy multi-instância ou a carga de presença forcem a mão.
- [ ] **T-14** — *Depois.* Metas não funcionais: usuários concorrentes esperados, latência aceitável, meta de disponibilidade. Números provisórios servem; o valor está em ter algo contra o que testar.

## F — Frontend

- [ ] **F-1** — *Bloqueante.* Ferramenta de build e roteador. Vite + React Router é o padrão convencional.
- [ ] **F-2** — *Bloqueante.* Divisão do gerenciamento de estado: o que cuida do estado de servidor, o que cuida do estado de cliente. O doc 04 explica por que devem ser ferramentas diferentes.
- [ ] **F-3** — *Bloqueante.* **Auditoria de contraste.** Meçam o texto de baixa opacidade contra o fundo escuro. Várias combinações entre `.45` e `.62` de opacidade provavelmente estão abaixo do WCAG AA. Estabeleçam uma opacidade mínima para elementos que carregam texto e apliquem de forma consistente.
- [ ] **F-4** — *Bloqueante.* Tratamento de `prefers-reduced-motion` para as quatro animações infinitas. Inegociável para uma tela que o usuário encara por 25 minutos.
- [ ] **F-5** — *Bloqueante.* Estratégia responsiva conforme **P-4**. O layout de três colunas do timer precisa de um comportamento de colapso definido.
- [ ] **F-6** — *Em breve.* Abordagem de estilização: CSS Modules, Tailwind ou CSS-in-JS. O protótipo usa estilos inline, o que é um artefato de protótipo, não uma recomendação — mas mantenham a camada de tokens como CSS custom properties de qualquer forma, para que tematização (**D-11**, cosméticos) continue barata.
- [ ] **F-7** — *Em breve.* Biblioteca de componentes: construir primitivos ou adotar uma biblioteca headless. A identidade visual é distintiva o bastante para que bibliotecas de componentes pesadas briguem com vocês.
- [ ] **F-8** — *Em breve.* Projetar as oito telas faltantes listadas no doc 04.
- [ ] **F-9** — *Em breve.* **Estados vazios.** Especificamente o perfil do primeiro dia. Esta é a primeira impressão de um produto motivacional e hoje ela mostra zeros.
- [ ] **F-10** — *Em breve.* Acessibilidade por teclado e indicadores de foco. O protótipo tem estilos de hover, mas nenhum anel de foco visível.
- [ ] **F-11** — *Em breve.* Comportamento do título da aba e do favicon durante um ciclo em execução — mostrar o tempo restante no título da aba é uma feature pequena com valor desproporcional para um timer em segundo plano.
- [ ] **F-12** — *Depois.* Internacionalização. Se o time é brasileiro, decidam agora se o português entra no lançamento; adaptar i18n depois é significativamente mais caro do que começar com ela.
- [ ] **F-13** — *Depois.* Upload de avatares — armazenamento, redimensionamento, moderação. O protótipo usa iniciais geradas, que é uma resposta permanente perfeitamente boa.

## Q — Qualidade & processo

- [ ] **Q-1** — *Bloqueante.* Definition of done. Inclua: testes escritos, migration revisada, documentação atualizada.
- [ ] **Q-2** — *Bloqueante.* Modelo de branches e regras de code review — revisores obrigatórios, e se o autor pode dar merge.
- [ ] **Q-3** — *Bloqueante.* Rastreador de issues e como um ticket se move pelo board.
- [ ] **Q-4** — *Em breve.* Estratégia de testes. No mínimo: testes unitários para as regras de progressão e para `deriveStage`, testes de integração com Testcontainers contra um Postgres real, e um punhado de testes ponta a ponta no loop principal. Metas de cobertura são menos úteis do que nomear quais camadas são inegociáveis.
- [ ] **Q-5** — *Em breve.* Propriedade. Quem é dono do backend, do frontend, do schema e das decisões de design? Times pequenos costumam pular isso e depois descobrem que o schema tem três autores.
- [ ] **Q-6** — *Em breve.* Manutenção da documentação — estes documentos ficam obsoletos em um mês, a menos que atualizá-los faça parte de **Q-1**.
- [ ] **Q-7** — *Depois.* Orçamento de performance e testes de carga.

---

## Sequenciamento sugerido

A ordem de dependências que desbloqueia mais trabalho mais rápido:

**1. Fundação do produto** — P-1, P-3, P-4, P-7
Nada pode ser estimado enquanto a linha do MVP e a plataforma-alvo não estiverem fixadas.

**2. Regras de domínio** — D-1, D-2, D-5, D-6, D-7, D-8
Estas são baratas de decidir e caras de adaptar depois. Elas também são insumo direto da sessão de ER, então resolvam antes dela.

**3. Sessão de ER** — usando o doc 03 como ponto de partida, informada pelo passo 2.

**4. Fundação técnica** — T-1 a T-5, A-1, A-2, F-1, F-2, Q-1 a Q-3
Tudo que é necessário para escrever o primeiro commit de verdade.

**5. Acessibilidade e lacunas de design** — F-3, F-4, F-5, F-9
Deliberadamente cedo. Os quatro ficam exponencialmente mais caros depois que a biblioteca de componentes existir.

**6. Jurídico** — L-1, L-2, L-3
O L-1 em particular deve ser resolvido antes que alguém estime a feature de música, porque a resposta pode eliminar a feature.

**7. Salas co-op** — R-1 a R-8, apenas quando a feature for de fato agendada.

## Itens de maior risco

Se vocês só forem atrás de cinco coisas esta semana:

| Item | Por que é perigoso |
|---|---|
| **P-1** Limite do MVP | Sem ele, o time constrói em largura em vez de profundidade e nada é entregue |
| **D-7** Streak/fuso horário | Lógica de streak ingênua quanto a fuso horário quase certamente produz bugs visíveis ao usuário, e corrompe os dados que toca |
| **L-1** Licenciamento de música | Bloqueio jurídico que pode eliminar uma feature planejada depois de ela ter sido desenhada e estimada |
| **R-1** Modelo de sala | "Timer compartilhado" e "timers paralelos com presença" diferem em uma ordem de grandeza de custo, e hoje ambos têm o mesmo nome |
| **F-3/F-4** Acessibilidade | A estética (baixa opacidade, movimento constante) está em tensão direta com acessibilidade, e a correção é muito mais barata antes do design system ser codificado do que depois |
