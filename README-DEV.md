# Guia de desenvolvimento — StarlightTimer

Este guia segue o contexto do projeto (`AGENTS.md` na raiz ou `Contexo.md` nas notas do Obsidian). O ER define o schema e o diagrama de macro-arquitetura define a topologia. As decisões D-1 a D-11 estão em `10-domain-rules-decisions.md` no Obsidian.

As camadas e os módulos usam nomes em inglês. A camada vem primeiro: `domain/progression`, `domain/session`, `app/session` e `adapters/session`. Nomes de domínio, métodos, campos e mensagens de exceção continuam em português; os sufixos de papel das classes usam inglês, como Service, Controller e Repository.

## 1. Arquitetura e dependências

O backend usa arquitetura hexagonal por módulo de feature, com eventos internos entre módulos e Kafka via transactional outbox para consumidores externos.

O fluxo de dependência aponta para o núcleo:

`adapters → app → domain`

- `domain` contém modelos, invariantes, eventos e exceções, usando apenas Java e tipos do próprio domínio. Não importa Spring, JPA, Redis ou Kafka.
- `app` contém portas e serviços que orquestram os casos de uso. Depende do domínio e das próprias portas. Serviços podem usar `@Service` e `@Transactional`; outros imports do Spring ficam fora dessa camada.
- `adapters` implementa as portas de saída e traduz entradas externas para chamadas às portas de entrada.
- `shared`, dentro de cada camada, contém tipos transversais e não importa módulos de feature. Beans e configurações transversais ficam em `config`.

Um módulo acessa outro apenas por sua porta de entrada pública ou por evento de domínio interno. Não importa modelos, entidades JPA, repositórios ou serviços internos de outro módulo.

## 2. Organização dos pacotes

O pacote base é `dev.amigoes.starlighttimer`. Os módulos do MVP são `identity`, `session`, `annotation`, `progression` e `social`. Tipos transversais ficam em `shared` dentro da camada correspondente; configurações transversais ficam em `config`.

Cada camada é dividida por módulo. Dentro do módulo, use as subpastas em inglês correspondentes ao papel da classe:

```text
dev.amigoes.starlighttimer/
├── domain/
│   ├── identity/
│   ├── session/
│   │   ├── entities/
│   │   ├── enums/
│   │   ├── events/
│   │   └── exceptions/
│   ├── annotation/
│   ├── progression/
│   │   ├── entities/
│   │   └── services/
│   ├── social/
│   └── shared/
│       └── exceptions/
├── app/
│   ├── session/
│   │   ├── dto/
│   │   ├── ports/
│   │   │   ├── in/
│   │   │   └── out/
│   │   └── usecases/
│   ├── progression/
│   └── shared/
│       └── ports/out/
├── adapters/
│   ├── session/
│   │   ├── in/web/
│   │   └── out/persistence/
│   ├── progression/
│   └── shared/
│       └── out/events/
├── config/
└── StarlighttimerApplication.java
```

Pacotes, pastas de camada e módulos usam inglês. Nomes de domínio, métodos, campos e mensagens de exceção usam português; sufixos que indicam o papel da classe usam inglês (Service, Controller, Repository, Mapper, Adapter, Entity, Request, Response, UseCase, Command, Port e Exception). Identificadores não têm acentos nem `ç`. Constantes de enum copiam exatamente os valores das migrations; nomes impostos por frameworks e o ponto de entrada gerado permanecem como estão.

| Papel | Exemplo |
| --- | --- |
| Modelo e evento | `Sessao`, `SessaoConcluida` |
| Porta de entrada e comando | `IniciarSessaoUseCase`, `IniciarSessaoCommand` |
| Serviço de aplicação | `IniciarSessaoService` |
| Porta de saída | `SalvarSessaoPort` |
| Persistência | `SessaoEntity`, `SessaoJpaRepository`, `SessaoMapper`, `SessaoPersistenceAdapter` |
| Web | `SessaoController`, `IniciarSessaoRequest`, `SessaoResponse` |
| Exceção | `DuracaoInvalidaException` |

A árvore mostra a convenção para os módulos do MVP; diretórios sem implementação não precisam de classes artificiais. `PublicadorEventosPort` fica em `app/shared/ports/out`, e seu adaptador Spring em `adapters/shared/out/events`. `SessaoConcluida` é o contrato público em `domain/session/events`; a progressão reconhece foco por `ehFoco()`, sem importar `domain/session/enums`.

## 3. Implementar um caso de uso

1. Modele as invariantes e transições em `domain/<module>`. Injete `Clock` para leituras de tempo; não use `Instant.now()` ou `LocalDate.now()` sem relógio.
2. Defina a porta de entrada e o comando em `app/<module>/ports/in`; declare as necessidades externas em `app/<module>/ports/out`.
3. Implemente a orquestração em `app/<module>/usecases`, com dependências recebidas pelo construtor e a transação necessária ao fluxo.
4. Implemente as portas de saída em `adapters/<module>/out`. Entidades JPA são separadas dos modelos de domínio, convertidas por mapeadores e declaram `@Table(name = ...)` explicitamente.
5. Implemente o controlador em `adapters/<module>/in/web`, dependendo da porta de entrada. Receba e devolva DTOs próprios (`record`), com Bean Validation para formato e obrigatoriedade.
6. Traduza exceções de domínio para `ProblemDetail` (RFC 9457) em `adapters/shared/in/web`. Invariantes de negócio permanecem no domínio.
7. Registre os passos de verificação manual e os resultados no PR.

Evite interfaces especulativas, mediadores genéricos, CQRS e event sourcing completo. Um catálogo somente leitura pode ter uma única porta de leitura.

### Persistência, cache e eventos

- Mudanças de schema começam no ER e viram uma nova migration Flyway. Nunca edite migrations já mergeadas em `dev`; mantenha `ddl-auto=validate`.
- PostgreSQL é a fonte da verdade. Redis usa cache-aside; escritas invalidam o cache e falha de cache é tratada como miss. A validação de conclusão de sessão lê estado autoritativo do banco.
- A progressão reage a eventos internos de conclusão, com XP consistente na mesma transação da sessão. O ledger `ganho_xp` é append-only e `perfil.xp_total` é seu cache transacional.
- Eventos externos são gravados no outbox na transação de negócio e publicados depois no Kafka. Consumidores precisam ser idempotentes. A tabela de outbox deve estar no ER antes da migration (BL-12).
- O aceite de amizade atualiza o status do pedido; o trigger cria a amizade e exclui o pedido. O adaptador conhece esse comportamento, faz flush e carrega a amizade para a resposta.

## 4. Ambiente local e verificação

Pré-requisitos: Java 21, Docker, Docker Compose e Git. Use sempre o Maven Wrapper do repositório.

Copie `.env.example` para `.env` e preencha as variáveis do ambiente. Não versione segredos.

```bash
cp .env.example .env
docker compose up -d
./mvnw -DskipTests compile
./mvnw -DskipTests spring-boot:run
```

No Windows, use `mvnw.cmd`:

```powershell
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd -DskipTests spring-boot:run
```

Para empacotar ou executar a verificação de build:

```bash
./mvnw -DskipTests clean package
./mvnw -DskipTests verify
```

**Política vigente (§10.1 do contexto):** até a entrega de 06/11/2026, assistentes de IA não escrevem, alteram nem executam testes automatizados. A restrição permanece até a seção ser explicitamente atualizada; a passagem da data não libera testes por si só.

Use verificação manual: requisições HTTP, consultas SQL e inspeção de logs, com resultado esperado e observado no PR. Não altere arquivos de teste, dependências ou plugins de teste durante essa restrição.

### Acesso local sem JWT

Sem perfil ativo explícito, a API usa o perfil `local`. Nesse perfil, ela cria no startup um usuário padrão (`00000000-0000-0000-0000-000000000001`) com perfil e configurações. Os endpoints de negócio usam esse usuário sem exigir autenticação nem cabeçalhos.

Para criar outro usuário local, chame `POST /api/v1/usuarios/local` com `email`, `nomeExibido` e `fusoHorario` IANA. A resposta `201` contém o `id`. Envie `X-Usuario-Id: <id>` nas chamadas seguintes para selecionar esse usuário. O cabeçalho é opcional e só funciona no perfil `local`; IDs inexistentes ou inativos recebem `404`. Fora desse perfil, o adaptador atual continua devolvendo `401` até a integração de JWT. Não use o perfil `local` em ambiente exposto a usuários externos.

```bash
curl -i http://localhost:8081/api/v1/me/perfil
curl -i -X POST http://localhost:8081/api/v1/usuarios/local \
  -H 'Content-Type: application/json' \
  -d '{"email":"outra-pessoa@example.test","nomeExibido":"Outra pessoa","fusoHorario":"America/Recife"}'
curl -i http://localhost:8081/api/v1/me/perfil -H 'X-Usuario-Id: <id-retornado>'
```

Para verificar um CRUD, crie uma anotação com `POST /api/v1/anotacoes` (`conteudo` e `dataAnotacao`), consulte com `GET /api/v1/anotacoes?data=YYYY-MM-DD`, edite com `PATCH /api/v1/anotacoes/{id}` (`conteudo`) e remova com `DELETE /api/v1/anotacoes/{id}`. Use o mesmo cabeçalho nas quatro chamadas se quiser operar como o segundo usuário. As rotas locais também aparecem em `/swagger-ui.html`.

## 5. Contribuição

- Use os templates de issue existentes. Preserve os títulos dos campos usados pela triagem automática.
- Crie a branch a partir de `dev`, no padrão `<tipo>/<numero-da-issue>-<descricao>`, como `fix/21-alinha-guia-contexto`.
- Use Conventional Commits: `<tipo>(<escopo>): <resumo>`. Os escopos de commit continuam em português conforme o contexto; são independentes dos nomes dos pacotes e incluem áreas como `docs`, `infra` e `ci`.
- Abra PR para `dev` e preencha o template. Use `Closes #N` quando o PR resolver a issue e cite IDs de decisões sem renomeá-los.
- Na seção de validação, marque os passos manuais realizados ou a revisão documental e descreva o resultado. Compilações usam `-DskipTests`.
- Atualize a documentação no mesmo PR quando mudar uma decisão ou fluxo e marque a opção correspondente no template.
- Não faça commit, push ou merge direto em `dev` ou `main`.

## 6. Checklist de revisão

- [ ] Classes estão na camada e no módulo corretos, com pacotes em inglês e identificadores de negócio em português?
- [ ] Domínio usa apenas Java e tipos do próprio domínio?
- [ ] Aplicação depende apenas do domínio e das próprias portas, respeitando a concessão para `@Service` e `@Transactional`?
- [ ] Comunicação entre módulos usa porta de entrada pública ou evento interno?
- [ ] Controladores dependem de portas de entrada e usam DTOs próprios?
- [ ] Entidades JPA e modelos de domínio estão separados por mapeadores?
- [ ] Tempo usa `Clock`, e enums seguem as migrations?
- [ ] Schema, cache, transações e eventos respeitam as fontes da verdade?
- [ ] O PR registra a validação realizada e respeita a política de testes?

## 7. Contrato declarativo das insígnias (D-9)

O seed `V6__seed_catalogos.sql` propõe a versão 1 de `insignia.criterio_json`. A migration guarda dados do catálogo; os avaliadores ainda serão implementados. `contagem_alvo` é o limiar numérico, e `xp_recompensa` é concedido uma única vez no desbloqueio.

| `tipo_criterio` | Medida para comparar com `contagem_alvo` | Campos da versão 1 |
| --- | --- | --- |
| `CONTAGEM_SESSOES` | Quantidade de sessões que passam pelos filtros | `filtros` e, quando aplicável, `janela_hora_local` |
| `MINUTOS_FOCO` | Soma do foco de sessões concluídas em minutos | `filtros`, `campo_soma`, `unidade_origem`, `unidade_alvo` |
| `ORBIT` | Maior sequência de dias lógicos consecutivos com estrela | `filtros`, `campo_dia`, `agregacao` |
| `DIAS_ATIVOS` | Dias lógicos distintos com estrela | `filtros`, `campo_dia`, `consecutivos` |
| `CONTAGEM_DESCANSOS` | Descansos concluídos vinculados a foco | `filtros`, `vinculo_foco` |
| `CONTAGEM_ANOTACOES` | Anotações de sessões concluídas | `sessao_vinculada` |
| `DIAS_ANOTACOES` | Dias lógicos distintos de criação de anotações | `campo_instante`, `fuso`, `virada_dia_local`, `consecutivos` |
| `ESPECIAL` | Medida da regra nomeada em `regra` | Parâmetros próprios de `CELESTIAL_CLOCKWORK`, `STABLE_FUSION`, `REIGNITION` ou `PHOENIX_FLAME` |

Todo JSON tem `versao: 1`. Em `filtros`, `tipo` e `status` usam os valores das migrations; `tempo_de_pausa: 0` restringe a sessão sem pausa. `janela_hora_local` usa `iniciado_em`, com limite inicial inclusivo e final exclusivo. `PERFIL_NA_AVALIACAO` significa interpretar o instante no fuso do perfil vigente na avaliação, conforme D-9; não altera `sessao.dia_local`, congelado no início pela D-7.

`campo_dia: "dia_local"` usa o dia lógico da sessão, cuja virada é às 04:00 locais. Em `MINUTOS_FOCO`, `tempo_de_foco` está em segundos e o alvo em minutos. `vinculo_foco` requer foco concluído do mesmo usuário e início do descanso entre 0 e 120 segundos depois do término do foco. `DIAS_ANOTACOES` deriva o dia lógico de `criado_em`, nunca de `data_anotacao`. Para `ESPECIAL`, o avaliador em código usa `regra` para selecionar uma das quatro regras de D-9; não há avaliador genérico de JSON implementado neste PR.

## 8. Concessão de progressão na conclusão (D-5, D-6, D-9 e D-11)

O caso de uso da sessão conclui e persiste `sessao` e seu evento na transação corrente, cria `SessaoConcluida` e chama `PublicadorEventosPort.publicar(evento)` antes do commit. O listener de progressão faz `flush` do contexto JPA e executa `ConcederProgressaoService` com propagação `MANDATORY`; assim, falhar a concessão desfaz também a conclusão. O publicador Spring é síncrono. Depois da publicação, `evento.recompensa()` contém `xpGanho`, os códigos das novas insígnias e constelações, `subiuDeNivel` e `novoNivel` para a resposta da conclusão.

O adaptador de progressão bloqueia a linha de `perfil` antes de ler o histórico, serializando concessões do usuário. O ledger usa os índices únicos de `ganho_xp` para tornar a concessão idempotente. Só depois de gravar as três possíveis origens (`origem_sessao`, `origem_insignia`, `origem_constelacao`) o serviço acrescenta o total concedido a `perfil.xp_total`, na mesma transação. Descanso não ganha XP por sessão, mas pode avançar Cooling Nebula. O histórico lê sessões `COMPLETED` do PostgreSQL, sem Redis, e avalia as 22 métricas de D-9. `insignia_usuario` guarda o melhor progresso, com `desbloqueado_em` explicitamente nulo antes do desbloqueio; `constelacao_usuario` só recebe linhas após cumprir insígnias e estrelas. Reprocessar o evento de foco retorna recompensa zero e não avança métricas; o descanso usa métricas absolutas e as chaves únicas de desbloqueio para não repetir recompensa.

Para verificar manualmente quando #31 estiver integrado: conclua um foco de 25 minutos, confira na resposta 75 XP, First Light e nível 2, e compare `perfil.xp_total` com `SUM(ganho_xp.quantidade)`. Publique de novo o mesmo evento e confirme que as contagens e o ledger não mudam. Conclua um descanso e confirme que não há linha `ganho_xp` com sua sessão como origem. Depois de dez focos e duas insígnias da First Firmament, confirme que o décimo foco desbloqueia a constelação uma única vez. Confira também que um foco abandonado não gera recompensa.

### Consulta do perfil de progressão (#35)

### API de sessão (#31)

`POST /api/v1/sessoes` recebe `tipo` (`FOCUS` ou `SHORT_BREAK`), `duracaoPlanejada` opcional e `reportadoEm` opcional. A duração padrão vem de `configuracoes_usuario`; `diaLocal` é calculado no fuso IANA do perfil, com virada às 04:00. A resposta 201 inclui `id`, `iniciadoEm`, `duracaoPlanejada` e `horarioServidor`. Uma sessão ativa devolve 409 com `sessaoId`, com o índice parcial do banco como garantia final.

`PATCH /api/v1/sessoes/{id}` recebe `status` (`PAUSED`, `RUNNING`, `ABANDONED` ou `COMPLETED`) e `reportadoEm` opcional. Cada transição grava `sessao_evento` com `origem=CLIENTE`; o horário informado pelo cliente nunca decide a conclusão. Um pedido de conclusão mais de cinco segundos antes do fim devolve 409 com `segundosRestantes` e preserva `CONCLUSAO_REJEITADA`. Uma conclusão válida devolve a recompensa concedida pela progressão na mesma transação. O job percorre sessões ativas a cada 30 segundos, conclui as vencidas e abandona as pausadas por mais de 600 segundos acumulados, com eventos de origem `AGENDADOR`.

`GET /api/v1/me/sessao-ativa` devolve 200 ou 204. `GET /api/v1/sessoes` aceita `pagina` a partir de zero, `tamanho` de 1 a 100 e filtros opcionais `dataInicio` e `dataFim` em ISO 8601, sobre `dia_local`. O resultado contém `itens`, `pagina`, `tamanho` e `proximaPagina`. O acesso a uma sessão de outro usuário devolve 404.

A autenticação JWT ainda não está integrada. No perfil `local`, esses endpoints usam o usuário padrão ou o UUID selecionado pelo cabeçalho opcional. Para verificar manualmente, inicie uma sessão e confira `INICIADA` e `dia_local` no banco; repita o POST e confira 409 com o mesmo ID; pause, retome e confira `ocorrido_em` e `reportado_em` distintos; tente concluir cedo e confira 409 e `CONCLUSAO_REJEITADA`; conclua no tempo e compare a recompensa com `ganho_xp`; depois confira a recuperação, a paginação filtrada, o 404 para sessão alheia e o encerramento pelo job. A compilação local usa `sh ./mvnw -DskipTests compile` enquanto o wrapper não tiver permissão de execução.

`GET /api/v1/me/progressao` pode ser consultado no perfil `local`, sem login. A resposta contém `nivel`, `patente` (`codigo`, `nome`, `imagemTitulo`), `xpTotal`, `xpAtual` dentro do nível, `xpDoProximoNivel` como limite acumulado e `xpParaProximoNivel` como saldo faltante. No nível 60, os dois campos de próximo nível são `null`. Também devolve `estrelasForjadas`, `segundosEmOrbit`, `horasEmOrbit` com duas casas decimais e `orbitAtual` em dias lógicos consecutivos. Para cada insígnia, a resposta inclui código, nome, descrição, ícone, contagem atual, alvo e instante de desbloqueio; para cada constelação, inclui o total de estrelas, a meta de estrelas, o número de insígnias do grupo desbloqueadas, a meta de insígnias e o instante de desbloqueio.

O caso de uso lê o perfil e os desbloqueios do PostgreSQL, os catálogos pelas portas existentes e as sessões concluídas pelo histórico da #32. `orbitAtual` considera o dia lógico vigente no fuso IANA do perfil, com virada às 04:00; enquanto o dia ainda não tem foco, a sequência encerrada no dia anterior continua visível. A data `sessao.dia_local` já foi fixada quando a sessão começou e não é recalculada após troca de fuso. A ausência de sessões produz nível 1, patente inicial, zero estrelas, zero tempo e zero Orbit, com os catálogos completos e progresso zero. Um UUID sem perfil recebe `404` em `ProblemDetail`.

Verificação manual: com a API no perfil `local`, chame `curl -i http://localhost:8081/api/v1/me/progressao`. Num banco local migrado até V6, um perfil sem sessões deve retornar nível 1, patente `STARGAZER`, XP zero, 22 insígnias e seis constelações. Duas sessões de foco concluídas em `dia_local` consecutivos devem retornar duas estrelas, 3.000 segundos, 0,83 hora e Orbit de dois dias. Um UUID inexistente no cabeçalho recebe `404`. Confira XP e desbloqueios contra `perfil`, `sessao`, `insignia_usuario` e `ganho_xp`; para a virada, consulte antes e depois das 04:00 no fuso do perfil.

### Perfil e configurações do próprio usuário (#28)

`GET` e `PATCH /api/v1/me/perfil` consultam e alteram nome de exibição, avatar, bio e fuso horário. O GET inclui `xpTotal`; o PATCH não altera esse cache do ledger. `GET` e `PATCH /api/v1/me/configuracoes` consultam e alteram foco, pausa, início automático da pausa e som. O fuso e as durações são validados pelo domínio. No perfil `local`, os quatro endpoints usam o usuário padrão ou o UUID do cabeçalho opcional.

Verificação manual: envie GET e PATCH aos quatro endpoints, com e sem `X-Usuario-Id`, e confirme que os dados pertencem ao usuário selecionado. Confira `404` para UUID inexistente, `400` para fuso ou durações inválidos e que editar o perfil não altera `perfil.xp_total` nem cria uma linha em `ganho_xp`.

## 9. Pedidos de amizade

`POST /api/v1/amizades/pedidos` recebe `destinatarioId` (UUID) e cria um pedido `PENDENTE` para o usuário atual. Remetente e destinatário precisam ter `usuario.status = ACTIVE` e `excluido_em` nulo; um destinatário inexistente, desativado ou excluído retorna 404. Um pedido pendente já existente para o par, em qualquer direção, retorna 409. A consulta de bloqueio e amizade considera as duas direções do par. A persistência serializa operações do mesmo par com um lock transacional no PostgreSQL.

`PATCH /api/v1/amizades/pedidos/{id}` recebe `acao` (`ACEITAR`, `RECUSAR` ou `CANCELAR`). Só o destinatário pode aceitar ou recusar; só o remetente pode cancelar. O serviço valida a transição de estado antes de atualizar a linha. Na aceitação, a aplicação altera somente `pedido_amizade.status`; o trigger da migration V5 cria a `amizade` com os UUIDs em ordem canônica e remove o pedido na mesma transação. O adaptador limpa o contexto JPA após o `UPDATE` e consulta a amizade para a resposta. Recusa e cancelamento mantêm o pedido com o status e `respondido_em` atualizados.

As ações usam `ConsultarIdentidadePort` para obter o usuário atual. No perfil `local`, crie um segundo usuário pelo endpoint local e selecione cada participante pelo cabeçalho `X-Usuario-Id`. Verifique envio, aceite, recusa, cancelamento e o efeito do trigger no PostgreSQL. Fora do perfil `local`, a identidade continua indisponível até BL-05.

### Círculo social (#34)

`GET /api/v1/me/amigos` devolve uma lista com `id`, `nomeExibido` e `avatar` do perfil de cada amigo. `GET /api/v1/me/pedidos` devolve `recebidos` e `enviados`, contendo apenas pedidos `PENDENTE`. A consulta de perfil público passa pela porta de entrada de identidade; o módulo social não acessa as entidades JPA de identidade.

`DELETE /api/v1/amizades/{id}` permite que qualquer participante remova a amizade e devolve `204`; para um ID inexistente ou de outra pessoa, devolve `404`. `POST /api/v1/me/bloqueios/{usuarioId}` grava o bloqueio, remove a amizade e marca os pedidos pendentes do par como `CANCELADO` com `respondido_em`, tudo na mesma transação. `DELETE` no mesmo caminho remove somente o bloqueio: não recria amizade nem pedido. Bloquear e desbloquear novamente devolvem `204`. Bloquear a si mesmo devolve `422`; bloquear um usuário inexistente ou inativo devolve `404`. As mutações usam o lock por par de usuários já adotado nos pedidos de amizade.

No perfil `local`, verifique com dois usuários: aceite um pedido, consulte as duas listas, desfaça a amizade de cada lado, crie novo pedido e bloqueie o par. Confira no PostgreSQL que a amizade sumiu, que o pedido passou a `CANCELADO` com `respondido_em` e que `lista_bloqueio` manteve a linha. Desbloqueie e confirme que a amizade continua ausente.

## 10. Anotações privadas

`POST`, `GET`, `PATCH` e `DELETE /api/v1/anotacoes` usam `ConsultarIdentidadePort` para obter o usuário atual. No perfil `local`, o cabeçalho `X-Usuario-Id` seleciona um usuário existente sem autenticação. A leitura, edição e exclusão de uma anotação alheia retornam `404`. A criação e a listagem por sessão também retornam `404` quando a sessão não pertence ao usuário selecionado.

`GET /api/v1/anotacoes` exige exatamente um filtro: `sessaoId` ou `data` no formato ISO `YYYY-MM-DD`. Enviar os dois filtros ou nenhum retorna `400`. Para verificar manualmente, crie anotações de dia e de sessão com dois usuários locais, confira que cada um só acessa as próprias linhas e valide os retornos `400` e `404` descritos acima.
