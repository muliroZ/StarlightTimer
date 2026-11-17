# StarlightTimer — notas do projeto

**Atualizado em 17/11/2026.** Aplicação Pomodoro com tema espacial. Backend em Java 21 + Spring Boot 4.1.1, PostgreSQL, Redis e Flyway; frontend previsto em TypeScript + React. O M0 entregou a fundação do backend. A implementação é integrada na `dev`, e a `dev-docs` reúne uma cópia de referência destas notas do Obsidian.

## Ordem de leitura e fontes da verdade

1. [Contexo](Contexo.md) — contexto geral, hierarquia de fontes, arquitetura, convenções e regras de IA. O nome do arquivo no Obsidian é `Contexo.md`.
2. [10-domain-rules-decisions](10-domain-rules-decisions.md) — decisões D-1 a D-11, catálogo inicial e regras de domínio. Para essas decisões, esta nota atualiza o contexto anterior.
3. [Diagrama ER](<Modelo ER - StarlightTimer(4).png>) — fonte máxima do schema; [Macro-arquitetura](Macro-arquitetura.md) — fonte máxima da topologia. O PNG é o ER de referência atual. As regras de identidade da #66 ainda precisam ser incorporadas ao desenho antes da migration #74.
4. Código, migrations e `pom.xml` — stack, pacote base e valores literais de enum.

## Índice

| Nota | Conteúdo |
| --- | --- |
| [01-product-overview](01-product-overview.md) | Visão, escopo do MVP e termos temáticos |
| [02-architecture](02-architecture.md) | Topologia e hexágonos por módulo |
| [03-domain-model](03-domain-model.md) | Entidades e regras do modelo |
| [04-frontend-design-system](04-frontend-design-system.md) | Tokens, telas e timer estelar |
| [05-planning-checklist](05-planning-checklist.md) | IDs de decisões resolvidas e em aberto |
| [06-git-workflow](06-git-workflow.md) | Branches, commits, PRs e CI |
| [07-workflow-additional-ideas](07-workflow-additional-ideas.md) | Ideias opcionais de colaboração |
| [08-entity-inventory](08-entity-inventory.md) | Tabelas do MVP e escopo posterior |
| [09-architecture-&-action-plan](09-architecture-%26-action-plan.md) | Notas complementares de arquitetura |
| [10-domain-rules-decisions](10-domain-rules-decisions.md) | D-1 a D-11 e catálogos iniciais |
| [Backlog](Backlog.md) | Entregas por dependência e critérios manuais |
| [Catálogo](Cat%C3%A1logo.md) | Ideias temáticas e referência histórica do catálogo |
| [Requests](Requests.md) | Rotas disponíveis para verificação local |

## Convenções

A interface usa *star*, *ignite*, *orbit* e outros termos temáticos. Nomes de domínio, métodos, campos e schema usam português; sufixos de papel das classes, pacotes, módulos e pastas usam inglês (`domain/session`, `domain/progression`), conforme [Contexo](Contexo.md) §6–7; os valores de enum copiam as migrations. Mudanças de schema começam no ER. A política Q-4 de testes está em [Contexo](Contexo.md) §10.1.
