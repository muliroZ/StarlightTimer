# Guia de desenvolvimento — StarlightTimer

Este guia segue o contexto do projeto (`AGENTS.md` na raiz ou `Contexo.md` nas notas do Obsidian). O ER define o schema e o diagrama de macro-arquitetura define a topologia. As decisões D-1 a D-11 estão em `10-domain-rules-decisions.md` no Obsidian.

A estrutura abaixo é a convenção adotada. O código existente ainda contém pacotes globais como `domain`, `app`, `adapters` e `config`; o alinhamento desses arquivos será feito em PR próprio. A existência desses pacotes não substitui a organização definida pelo contexto.

## 1. Arquitetura e dependências

O backend usa arquitetura hexagonal por módulo de feature, com eventos internos entre módulos e Kafka via transactional outbox para consumidores externos.

O fluxo de dependência aponta para o núcleo:

`adaptador → aplicacao → dominio`

- `dominio` contém modelos, invariantes, eventos e exceções, usando apenas Java e tipos do próprio domínio. Não importa Spring, JPA, Redis ou Kafka.
- `aplicacao` contém portas e serviços que orquestram os casos de uso. Depende do domínio e das próprias portas. Serviços podem usar `@Service` e `@Transactional`; outros imports do Spring ficam fora dessa camada.
- `adaptador` implementa as portas de saída e traduz entradas externas para chamadas às portas de entrada.
- `compartilhado` contém configurações e infraestrutura transversais e não importa módulos de feature.

Um módulo acessa outro apenas por sua porta de entrada pública ou por evento de domínio interno. Não importa modelos, entidades JPA, repositórios ou serviços internos de outro módulo.

## 2. Organização dos pacotes

O pacote base é `dev.amigoes.starlighttimer`. Os módulos do MVP são `identidade`, `sessao`, `anotacao`, `progressao` e `social`.

Cada módulo tem seu próprio hexágono, como neste exemplo:

```text
dev.amigoes.starlighttimer/
├── identidade/
├── sessao/
│   ├── dominio/
│   │   ├── modelo/
│   │   ├── evento/
│   │   └── excecao/
│   ├── aplicacao/
│   │   ├── porta/
│   │   │   ├── entrada/
│   │   │   └── saida/
│   │   └── servico/
│   └── adaptador/
│       ├── entrada/
│       │   └── web/
│       └── saida/
│           ├── persistencia/
│           ├── cache/
│           └── mensageria/
├── anotacao/
├── progressao/
├── social/
├── compartilhado/
│   ├── dominio/
│   ├── web/
│   ├── config/
│   ├── seguranca/
│   └── outbox/
└── StarlighttimerApplication.java
```

Código Java, incluindo pacotes, classes, métodos, campos e mensagens de exceção, usa português. Identificadores não têm acentos nem `ç`. Constantes de enum copiam exatamente os valores das migrations; nomes impostos por frameworks e o ponto de entrada gerado permanecem como estão.

| Papel | Exemplo |
| --- | --- |
| Modelo e evento | `Sessao`, `SessaoConcluida` |
| Porta de entrada e comando | `IniciarSessaoCasoDeUso`, `IniciarSessaoComando` |
| Serviço de aplicação | `IniciarSessaoServico` |
| Porta de saída | `SalvarSessaoPorta` |
| Persistência | `SessaoEntidade`, `SessaoRepositorioJpa`, `SessaoMapeador`, `SessaoAdaptadorPersistencia` |
| Web | `SessaoControlador`, `IniciarSessaoRequisicao`, `SessaoResposta` |
| Exceção | `DuracaoInvalidaExcecao` |

## 3. Implementar um caso de uso

1. Modele as invariantes e transições em `<modulo>/dominio`. Injete `Clock` para leituras de tempo; não use `Instant.now()` ou `LocalDate.now()` sem relógio.
2. Defina a porta de entrada e o comando em `aplicacao/porta/entrada`; declare as necessidades externas em `aplicacao/porta/saida`.
3. Implemente a orquestração em `aplicacao/servico`, com dependências recebidas pelo construtor e a transação necessária ao fluxo.
4. Implemente as portas de saída em `adaptador/saida`. Entidades JPA são separadas dos modelos de domínio, convertidas por mapeadores e declaram `@Table(name = ...)` explicitamente.
5. Implemente o controlador em `adaptador/entrada/web`, dependendo da porta de entrada. Receba e devolva DTOs próprios (`record`), com Bean Validation para formato e obrigatoriedade.
6. Traduza exceções de domínio para `ProblemDetail` (RFC 9457) em `compartilhado/web`. Invariantes de negócio permanecem no domínio.
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
- Use Conventional Commits: `<tipo>(<escopo>): <resumo>`. Escopos incluem os módulos em português e áreas como `docs`, `infra` e `ci`.
- Abra PR para `dev` e preencha o template. Use `Closes #N` quando o PR resolver a issue e cite IDs de decisões sem renomeá-los.
- Na seção de validação, marque os passos manuais realizados ou a revisão documental e descreva o resultado. Compilações usam `-DskipTests`.
- Atualize a documentação no mesmo PR quando mudar uma decisão ou fluxo e marque a opção correspondente no template.
- Não faça commit, push ou merge direto em `dev` ou `main`.

## 6. Checklist de revisão

- [ ] Classes estão no módulo correto, com pacotes e identificadores em português?
- [ ] Domínio usa apenas Java e tipos do próprio domínio?
- [ ] Aplicação depende apenas do domínio e das próprias portas, respeitando a concessão para `@Service` e `@Transactional`?
- [ ] Comunicação entre módulos usa porta de entrada pública ou evento interno?
- [ ] Controladores dependem de portas de entrada e usam DTOs próprios?
- [ ] Entidades JPA e modelos de domínio estão separados por mapeadores?
- [ ] Tempo usa `Clock`, e enums seguem as migrations?
- [ ] Schema, cache, transações e eventos respeitam as fontes da verdade?
- [ ] O PR registra a validação realizada e respeita a política de testes?
