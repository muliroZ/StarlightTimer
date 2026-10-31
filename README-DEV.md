# 🛠️ Guia de Desenvolvimento — StarlightTimer (`README-DEV.md`)

Bem-vindo(a) ao guia técnico do **StarlightTimer**! Este documento descreve a nossa arquitetura, organização de pacotes, regras de dependência e o fluxo de trabalho para contribuir com o projeto.

---

## 🏛️ 1. Arquitetura do Projeto (Arquitetura Hexagonal)

Utilizamos a **Arquitetura Hexagonal (Ports and Adapters)** com uma abordagem **híbrida (Feature-based nos Casos de Uso)**. O objetivo principal é isolar o coração do negócio (`domain` e `app`) de detalhes técnicos externos, frameworks, banco de dados e interfaces de usuário (`adapters` e `config`).

### Regra de Ouro de Dependência
> **O fluxo de dependência aponta sempre para o centro (Domínio).**  
> `adapters` / `config` ➔ `app` ➔ `domain`

* O pacote **`domain`** não conhece ninguém. Deve ser **100% Java puro** (sem anotações do Spring, JPA/Hibernate, Jackson, etc.).
* O pacote **`app`** conhece apenas o `domain`. Ele define as interfaces (**Ports**) pelas quais o mundo externo conversa com a aplicação.
* O pacote **`adapters`** conhece `app` e `domain`, implementando os detalhes técnicos (Controllers REST, entidades JPA, clientes externos).
* O pacote **`config`** enxerga todas as camadas para fazer a injeção de dependência (Beans do Spring) e configurações globais.

---

## 📂 2. Estrutura de Pacotes (`dev.amigoes.starlighttimer`)

Abaixo está o mapa da nossa árvore principal em `src/main/java/dev/amigoes/starlighttimer/`:

```text
dev.amigoes.starlighttimer/
├── domain/                          # 💎 NÚCLEO DE NEGÓCIO (Java Puro)
│   ├── entities/                    # Entidades de domínio e regras de negócio intrínsecas
│   ├── enums/                       # Enumerações de domínio
│   └── exceptions/                  # Exceções de regra de negócio
│
├── app/                             # ⚙️ CAMADA DE APLICAÇÃO (Orquestração)
│   ├── usecases/                    # Organizado por FEATURE / CONTEXTO
│   │   ├── timer/                   # Exemplo de feature: Timer
│   │   │   ├── StartTimerUseCase.java
│   │   │   └── StopTimerUseCase.java
│   │   └── user/                    # Exemplo de feature: Usuário
│   │       └── RegisterUserUseCase.java
│   ├── dto/                         # Objetos de transferência da camada de aplicação (Commands/Queries/Outputs)
│   └── ports/                       # Fronteira do Hexágono (Interfaces/Contratos)
│       ├── in/                      # Driving Ports (Contratos que os Use Cases implementam)
│       └── out/                     # Driven Ports (Contratos que os Use Cases consomem, ex: Repositories)
│
├── adapters/                        # 🔌 MUNDO EXTERNO (Frameworks & I/O)
│   ├── in/                          # Quem aciona a aplicação (Primary/Driving Adapters)
│   │   └── web/                     # Controllers REST, Request/Response DTOs da Web, ExceptionHandlers
│   └── out/                         # Quem é acionado pela aplicação (Secondary/Driven Adapters)
│       └── persistence/             # Repositórios Spring Data JPA, Entidades ORM (@Entity) e Mappers
│
├── config/                          # 🧩 CONFIGURAÇÃO & INFRAESTRUTURA
│   └── ...                          # @Configuration, registro de @Bean dos UseCases, Security, Swagger/OpenAPI
│
└── StarlighttimerApplication.java   # Ponto de entrada do Spring Boot
```

---

## 🔄 3. Fluxo de uma Requisição (Passo a Passo)

Para criar um novo endpoint ou funcionalidade, siga o caminho abaixo:

1. **Domínio (`domain`):**
   * Crie ou atualize a entidade em `domain/entities` com suas validações puras e comportamentos.
   * Se necessário, adicione novos `enums` ou `exceptions` de negócio.
2. **Portas e DTOs de Aplicação (`app/ports` e `app/dto`):**
   * Crie os Records/DTOs de entrada e saída em `app/dto` (podem ser agrupados por subpacotes de feature se crescerem).
   * Defina a interface da **Porta de Entrada** em `app/ports/in` (ex: `CreateTimerInputPort`).
   * Se precisar buscar/salvar dados no banco, defina o método na interface da **Porta de Saída** em `app/ports/out` (ex: `TimerRepositoryOutputPort`).
3. **Caso de Uso (`app/usecases/<feature>`):**
   * Dentro da pasta da *feature* correspondente em `app/usecases/<feature>`, crie a classe do Caso de Uso implementando a `InputPort` e injetando a `OutputPort` via construtor.
   * **Atenção:** Evite acoplamento forte com o framework aqui; prefira declarar os `@Bean` dos casos de uso no pacote `config` (ou use apenas anotações mínimas caso o time decida padronizar `@Service`).
4. **Adaptador de Saída (`adapters/out/persistence`):**
   * Crie/atualize a entidade do banco (`@Entity` JPA), a interface do `SpringDataRepository` e o `Mapper` (que converte entre `Domain Entity` ⇄ `JPA Entity`).
   * Implemente a interface de `app/ports/out` chamando o repositório JPA.
5. **Adaptador de Entrada (`adapters/in/web`):**
   * Crie o `@RestController` injetando a **interface** de `app/ports/in`.
   * Receba o Request DTO (com validações `@Valid` do Jakarta Validation se aplicável), converta para o DTO da camada `app`, invoque a porta de entrada e retorne o Response HTTP adequado.

---

## 🚀 4. Configuração do Ambiente Local

### Pré-requisitos
* **Java JDK** (versão configurada no `pom.xml`, ex: Java 17 ou 21)
* **Docker** e **Docker Compose**
* *(Opcional)* Maven instalado globalmente — o projeto já inclui o **Maven Wrapper** (`./mvnw` e `mvnw.cmd`).

### Passo a Passo para Rodar

1. **Variáveis de Ambiente:**
   Copie o arquivo `.env.example` para `.env` na raiz do projeto e preencha os valores necessários:
   ```bash
   cp .env.example .env
   ```

2. **Subir Infraestrutura (Banco de Dados e Serviços):**
   Utilize o Docker Compose para iniciar os containers de apoio:
   ```bash
   docker compose up -d
   ```

3. **Compilar e Executar a Aplicação:**
   * No Linux / macOS:
     ```bash
     ./mvnw clean spring-boot:run
     ```
   * No Windows (PowerShell / CMD):
     ```powershell
     .\mvnw.cmd clean spring-boot:run
     ```

4. **Executar os Testes:**
   ```bash
   ./mvnw test
   ```

---

## 🌿 5. Fluxo de Contribuição, Issues e Pull Requests

O repositório conta com automações no GitHub Actions (`.github/workflows/issue-triage.yml` e `pr-check.yml`) que validam o padrão das entregas:

* **Issues:** Sempre utilize os templates oficiais ao abrir uma Issue (`bug_report.yml`, `feature.yml` ou `task.yml`). Issues em branco estão desabilitadas.
* **Branches:** Crie branches seguindo o padrão semântico vinculado à tarefa, por exemplo:
  * `feat/<numero-da-issue>-descricao-curta`
  * `fix/<numero-da-issue>-descricao-curta`
  * `chore/<numero-da-issue>-descricao-curta`
* **Pull Requests:**
  * Preencha completamente o template de PR (`.github/pull_request_template.md`).
  * O script de verificação automática (`pr-check.js`) validará a estrutura do seu PR antes do merge.
  * Certifique-se de que `./mvnw test` passa localmente antes de abrir ou atualizar o PR.

---

## ✅ 6. Checklist Rápido de Code Review (Arquitetura)

Antes de aprovar ou abrir um PR, verifique:
- [ ] Alguma classe em `domain` importou pacotes de `org.springframework.*`, `jakarta.persistence.*` ou `adapters.*`? *(Se sim, remova!)*
- [ ] O `Controller` em `adapters/in/web` está conversando com a interface em `app/ports/in` em vez de chamar o repositório direto?
- [ ] A entidade do banco (`@Entity`) está isolada em `adapters/out/persistence` e separada da entidade de negócio (`domain/entities`)?
- [ ] O novo Caso de Uso foi colocado dentro de uma subpasta de *feature* semântica em `app/usecases/<feature>`?