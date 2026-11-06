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

O caso de uso da sessão conclui e persiste `sessao` e seu evento na transação corrente, cria `SessaoConcluida` e chama `PublicadorEventosPort.publicar(evento)` antes do commit. O listener de progressão faz `flush` do contexto JPA e executa `ConcederProgressaoService` com propagação `MANDATORY`; assim, falhar a concessão desfaz também a conclusão. O publicador Spring é síncrono. Depois da publicação, `evento.recompensa()` contém `xpGanho`, os códigos das novas insígnias e constelações, `subiuDeNivel` e `novoNivel` para a resposta da conclusão de #31. A integração HTTP será feita junto com o controller dessa issue.

O adaptador de progressão bloqueia a linha de `perfil` antes de ler o histórico, serializando concessões do usuário. O ledger usa os índices únicos de `ganho_xp` para tornar a concessão idempotente. Só depois de gravar as três possíveis origens (`origem_sessao`, `origem_insignia`, `origem_constelacao`) o serviço acrescenta o total concedido a `perfil.xp_total`, na mesma transação. Descanso não ganha XP por sessão, mas pode avançar Cooling Nebula. O histórico lê sessões `COMPLETED` do PostgreSQL, sem Redis, e avalia as 22 métricas de D-9. `insignia_usuario` guarda o melhor progresso, com `desbloqueado_em` explicitamente nulo antes do desbloqueio; `constelacao_usuario` só recebe linhas após cumprir insígnias e estrelas. Reprocessar o evento de foco retorna recompensa zero e não avança métricas; o descanso usa métricas absolutas e as chaves únicas de desbloqueio para não repetir recompensa.

Para verificar manualmente quando #31 estiver integrado: conclua um foco de 25 minutos, confira na resposta 75 XP, First Light e nível 2, e compare `perfil.xp_total` com `SUM(ganho_xp.quantidade)`. Publique de novo o mesmo evento e confirme que as contagens e o ledger não mudam. Conclua um descanso e confirme que não há linha `ganho_xp` com sua sessão como origem. Depois de dez focos e duas insígnias da First Firmament, confirme que o décimo foco desbloqueia a constelação uma única vez. Confira também que um foco abandonado não gera recompensa.

### Consulta do perfil de progressão (#35)

`GET /api/v1/me/progressao` retorna `401` até a integração de JWT. O cabeçalho provisório `X-Usuario-Id` foi desativado porque não comprova a identidade. Quando a autenticação estiver disponível, a resposta contém `nivel`, `patente` (`codigo`, `nome`, `imagemTitulo`), `xpTotal`, `xpAtual` dentro do nível, `xpDoProximoNivel` como limite acumulado e `xpParaProximoNivel` como saldo faltante. No nível 60, os dois campos de próximo nível são `null`. Também devolve `estrelasForjadas`, `segundosEmOrbit`, `horasEmOrbit` com duas casas decimais e `orbitAtual` em dias lógicos consecutivos. Para cada insígnia, a resposta inclui código, nome, descrição, ícone, contagem atual, alvo e instante de desbloqueio; para cada constelação, inclui o total de estrelas, a meta de estrelas, o número de insígnias do grupo desbloqueadas, a meta de insígnias e o instante de desbloqueio.

O caso de uso lê o perfil e os desbloqueios do PostgreSQL, os catálogos pelas portas existentes e as sessões concluídas pelo histórico da #32. `orbitAtual` considera o dia lógico vigente no fuso IANA do perfil, com virada às 04:00; enquanto o dia ainda não tem foco, a sequência encerrada no dia anterior continua visível. A data `sessao.dia_local` já foi fixada quando a sessão começou e não é recalculada após troca de fuso. A ausência de sessões produz nível 1, patente inicial, zero estrelas, zero tempo e zero Orbit, com os catálogos completos e progresso zero. Um UUID sem perfil recebe `404` em `ProblemDetail`.

Verificação manual atual: com a API em execução, chame `curl -i -H 'X-Usuario-Id: <uuid>' http://localhost:8081/api/v1/me/progressao` e confirme `401`. Antes da desativação do cabeçalho provisório, num banco local migrado até V6, um perfil sem sessões retornou nível 1, patente `STARGAZER`, XP zero, 22 insígnias e seis constelações. Duas sessões de foco concluídas em `dia_local` consecutivos retornaram duas estrelas, 3.000 segundos, 0,83 hora e Orbit de dois dias. Um UUID sem perfil retornou `404`. Após a integração de JWT e da conclusão de sessão na API (#31), confira XP e desbloqueios contra `perfil`, `sessao`, `insignia_usuario` e `ganho_xp`; para a virada, consulte antes e depois das 04:00 no fuso do perfil.

### Perfil e configurações do próprio usuário (#28)

`GET` e `PATCH /api/v1/me/perfil` consultam e alteram nome de exibição, avatar, bio e fuso horário. O GET inclui `xpTotal`; o PATCH não altera esse cache do ledger. `GET` e `PATCH /api/v1/me/configuracoes` consultam e alteram foco, pausa, início automático da pausa e som. O fuso e as durações são validados pelo domínio. Enquanto JWT não estiver integrado, os quatro endpoints retornam `401`, mesmo com um cabeçalho de ID enviado pelo cliente.

Verificação manual atual: envie GET e PATCH aos quatro endpoints, com e sem `X-Usuario-Id`, e confirme `401` em `ProblemDetail`. Após a integração de JWT, confirme `404` para um usuário autenticado sem a respectiva linha, `400` para fuso ou durações inválidos e que editar o perfil não altera `perfil.xp_total` nem cria uma linha em `ganho_xp`.
