# StarlightTimer

A cozy, space-themed Pomodoro web application with gamified progression, co-op focus rooms, and a timer that visualises a 25-minute cycle as the life cycle of a star.

**Status:** planning / pre-implementation. UI prototype exists for Homepage and Profile. Backend not started.

**Stack:** Java 21 + Spring Boot · TypeScript + React · PostgreSQL

---

## Documentation index

| Doc | What it covers | Read it when |
|---|---|---|
| [01 — Product Overview](01-product-overview.md) | Vision, personas, feature catalogue, MVP boundary, domain glossary | You're new to the project |
| [02 — Architecture](02-architecture.md) | System context, containers, module map, key decisions and trade-offs, core data flows | You're about to write backend code |
| [03 — Domain Model](03-domain-model.md) | Candidate entities, relationships, and open modelling questions — input for the team's ER session | You're designing the schema |
| [04 — Frontend & Design System](04-frontend-design-system.md) | Design tokens extracted from the prototype, screen inventory, component decomposition | You're building UI |
| [05 — Planning Checklist](05-planning-checklist.md) | Every decision still open, grouped and prioritised | Every planning meeting |
| [06 — Git Workflow](06-git-workflow.md) | Guide explaining the Git/GitHub workflow of this project | Before your first branch |

## How to use these docs

These are **macro-level** documents. They describe shape, boundaries, and unresolved questions — not implementation detail. They deliberately stop short of API signatures, table DDL, and component props, because those decisions belong to the team and haven't been made yet.

Anything written as a **recommendation** is a suggestion with reasoning attached, not a settled decision. Anything in [05 — Planning Checklist](docs/05-planning-checklist.md) is an acknowledged gap.

## Conventions

- Domain language is thematic and load-bearing. A completed focus session is a *star forged*; a streak is an *orbit*. See the glossary in doc 01 and use these terms consistently in code, tickets, and UI copy.
- Diagrams are Mermaid, rendered inline by GitHub/GitLab.
- Docs live next to the code and are updated in the same pull request as the change they describe.
