# StarlightTimer

Uma aplicação web de Pomodoro aconchegante, com tema espacial, progressão gamificada, salas de foco em co-op e um timer que visualiza um ciclo de 25 minutos como o ciclo de vida de uma estrela.

**Status:** planejamento / pré-implementação. Existe protótipo de UI para as telas de Homepage e Perfil. Backend não iniciado.

**Stack:** Java 21 + Spring Boot · TypeScript + React · PostgreSQL

---

## Índice da documentação

| Doc | O que cobre | Leia quando |
|---|---|---|
| [01 — Visão Geral do Produto](docs/01-product-overview.md) | Visão, personas, catálogo de features, limite do MVP, glossário de domínio | Você é novo no projeto |
| [02 — Arquitetura](docs/02-architecture.md) | Contexto do sistema, containers, mapa de módulos, decisões-chave e trade-offs, fluxos de dados principais | Você vai escrever código de backend |
| [03 — Modelo de Domínio](docs/03-domain-model.md) | Entidades candidatas, relacionamentos e questões de modelagem em aberto — insumo para a sessão de ER do time | Você vai projetar o schema |
| [04 — Frontend & Design System](docs/04-frontend-design-system.md) | Design tokens extraídos do protótipo, inventário de telas, decomposição de componentes | Você vai construir UI |
| [05 — Checklist de Planejamento](docs/05-planning-checklist.md) | Toda decisão ainda em aberto, agrupada e priorizada | Toda reunião de planejamento |
| [06 — Fluxo de Git & Colaboração](docs/06-git-workflow.md) | Modelo de branches, proteção de branches, ciclo de desenvolvimento, commits, PRs, CI | Antes da sua primeira branch |
| [07 — Ideias Adicionais de Workflow](docs/07-workflow-additional-ideas.md) | Sugestões opcionais de automação e gestão | O processo atual começou a doer em algum ponto |

## Como usar estes documentos

Estes são documentos de **nível macro**. Eles descrevem forma, fronteiras e questões não resolvidas — não detalhes de implementação. Eles param deliberadamente antes de assinaturas de API, DDL de tabelas e props de componentes, porque essas decisões pertencem ao time e ainda não foram tomadas.

Tudo escrito como **recomendação** é uma sugestão com o raciocínio anexado, não uma decisão fechada. Tudo que está no [05 — Checklist de Planejamento](docs/05-planning-checklist.md) é uma lacuna reconhecida.

## Convenções

- A linguagem de domínio é temática e estrutural. Uma sessão de foco concluída é uma *star forged*; uma sequência de dias ativos é uma *orbit*. Veja o glossário no doc 01 e use esses termos de forma consistente em código, tickets e textos de UI.
- Diagramas são em Mermaid, renderizados inline pelo GitHub/GitLab.
- A documentação vive ao lado do código e é atualizada no mesmo pull request da mudança que descreve.

## Nota sobre esta tradução

Esta é a versão em português do conjunto de documentos. Os nomes dos arquivos e todos os IDs de checklist (**P-1**, **D-7**, **AD-2**, ...) foram mantidos idênticos à versão em inglês, para que referências em tickets, commits e conversas continuem funcionando nas duas versões.

Os termos do glossário temático (*star*, *ignite*, *orbit*, *stars forged*) foram mantidos em inglês de propósito: eles são a linguagem ubíqua que vai aparecer em nomes de classes, colunas de banco e eventos de domínio. Traduzi-los aqui quebraria a ligação entre a documentação e o código. Se o texto da **interface** deve ser em português é uma decisão separada — veja **F-12** no checklist.
