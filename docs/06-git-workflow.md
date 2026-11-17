# 06 — Fluxo de Git & Guia de Colaboração

Este documento explica como o time leva código de uma ideia até a `main`. Foi escrito para todo colaborador — leia antes da sua primeira branch, e releia se o fluxo parecer ambíguo no meio de um PR.

O fluxo de branches e review já foi decidido (Q-2) e há templates e triagem de issues (Q-3). Consulte [Contexo](Contexo.md) §9–10 para as regras vigentes; T-1 e T-7 ainda estão em aberto.

---

## 1. Modelo de branches

O fluxo de código tem três níveis. A branch `dev-docs` é uma referência de leitura com cópias das notas do Obsidian. Atualize apenas a documentação a partir da própria `dev-docs`, mantendo seu histórico independente.

| Branch     | Papel                                                                           | Protegida?              | Quem faz merge nela                                    |
| ---------- | ------------------------------------------------------------------------------- | ----------------------- | ------------------------------------------------------ |
| `main`     | Sempre implantável. Reflete um módulo de desenvolvimento finalizado e revisado. | Sim — ruleset rígido    | Apenas via PR revisado a partir de `dev`               |
| `dev`      | Branch de integração do módulo atualmente em andamento.                         | Sim — ruleset mais leve | Apenas via PR revisado a partir de branches `<tipo>/*` |
| `<tipo>/*` | Onde o trabalho de fato acontece. Curta duração, um tópico cada.                | Não                     | N/A — esta *é* a branch de trabalho                    |

```mermaid
gitGraph
    commit id: "baseline modulo 0"
    branch dev
    checkout dev
    commit id: "baseline dev"
    branch feat/20-dominio-sessao
    checkout feat/20-dominio-sessao
    commit id: "esqueleto do timer"
    commit id: "verificacao no servidor"
    checkout dev
    merge feat/20-dominio-sessao
    branch fix/21-desvio-timer
    checkout fix/21-desvio-timer
    commit id: "limita desvio de relogio"
    checkout dev
    merge fix/21-desvio-timer
    branch docs/22-convencoes-api
    checkout docs/22-convencoes-api
    commit id: "documenta convencoes T-9"
    checkout dev
    merge docs/22-convencoes-api
    checkout main
    merge dev tag: "modulo 1 concluido"
```

### Nomenclatura de branch: `<tipo>/<número-da-issue>-<descricao-curta>`

| Tipo | Usar para | Exemplo |
|---|---|---|
| `feat` | Uma nova feature ou capacidade | `feat/42-iniciar-sessao` |
| `fix` | Correção de bug | `fix/43-corrigir-dia-logico` |
| `docs` | Mudanças apenas de documentação | `docs/44-atualizar-modelo` |
| `refactor` | Reestruturação de código sem mudança de comportamento | `refactor/45-extrair-estagio` |
| `test` | Adicionar ou corrigir apenas testes | `test/46-ledger-xp` (somente após revisão de §10.1) |
| `chore` | Ferramental, dependências, config, CI | `chore/47-configurar-flyway` |
| `style` | Apenas formatação, sem mudança de lógica | `style/48-formatar-frontend` |

Mantenha a descrição curta, em kebab-case, e específica o bastante para que um colega consiga adivinhar o conteúdo só pelo nome. O repositório já usa GitHub Issues (Q-3). Inclua sempre o número da issue: `feat/42-iniciar-sessao`.

---

## 2. Rulesets de proteção de branch

Configure isso em GitHub repo settings → Rules → Rulesets, tanto para `main` quanto para `dev`.

**`main`** — o ruleset rígido:
- Exigir um pull request antes do merge. Sem pushes diretos, inclusive de admins.
- Exigir ao menos uma aprovação em review (suba para duas se o time for grande o suficiente para sustentar isso).
- Exigir que os status checks passem — a CI precisa estar verde (veja §5).
- Exigir que as branches estejam atualizadas antes do merge.
- Bloquear force pushes e exclusão da branch.
- Opcionalmente: exigir histórico linear, para que o log da `main` se leia como um módulo por merge, e não como um emaranhado.

**`dev`** — uma versão mais leve da mesma ideia:
- Exigir um pull request antes do merge (ninguém dá push direto, nem para consertar algo pequeno — é para isso que existe `fix/*`).
- Exigir ao menos uma aprovação em review.
- Exigir CI verde.
- Force pushes e exclusão podem continuar bloqueados aqui também — raramente há um bom motivo para reescrever o histórico da `dev`.

O objetivo de proteger a `dev` além da `main` é que um merge ruim na `dev` ainda trava todo mundo que está construindo em cima dela pelo resto do módulo. Proteção não é só para a branch que vai para produção — é para a branch da qual o trabalho dos outros depende.

---

## 3. O ciclo de desenvolvimento, passo a passo

1. **Pegue um pedaço de trabalho.** O ideal é que ele corresponda a um item do doc 05 ou a uma issue rastreada (**Q-3**).
2. **Crie a branch a partir de `dev`**, nunca de `main`:
   ```bash
   git checkout dev
   git pull origin dev
   git checkout -b feat/42-iniciar-sessao
   ```
3. **Trabalhe na branch.** Faça commits cedo e com frequência, usando Conventional Commits (§4). Dê push regularmente para que os colegas vejam o progresso e para que você não perca trabalho.
4. **Mantenha a branch atualizada.** Se a `dev` andar enquanto você trabalha, faça rebase (ou merge) dela antes de abrir o PR, para que o review aconteça sobre um diff limpo:
   ```bash
   git fetch origin
   git rebase origin/dev
   ```
5. **Abra um PR para `dev`**, não para `main`. Preencha a descrição (§5), vincule o item do checklist ou a issue relacionada.
6. **Revise e itere.** Ao menos um colega revisa; responda aos comentários com novos commits (não faça force-push no meio do review — isso esconde o que mudou desde a última olhada).
7. **Faça o merge e apague a branch.** Squash-merge é recomendado aqui apenas se o PR tiver vários commits menores, para que o histórico da `dev` se leia como um commit por feature em vez de cada "wip" intermediário. Se não for o caso, siga com o Merge Commit tradicional.
8. **Repita de 2 a 7** para cada feature, correção e mudança de documentação que pertença ao módulo de desenvolvimento atual.
9. **Quando o módulo estiver completo**, abra um PR de `dev` para `main`. Este é o review de maior peso — trate-o como um checkpoint, não como formalidade: o módulo realmente funciona ponta a ponta, a documentação está atualizada, o checklist reflete a realidade?
10. **Faça o merge na `main`** assim que aprovado e com a CI verde. Opcionalmente marque o commit de merge com uma tag (`v0.1.0`, `v0.2.0`, ...) para que a fronteira do módulo fique visível no histórico do repositório, e não apenas na memória.

---

## 4. Mensagens de commit — Conventional Commits

Formato:

```
<tipo>(<escopo>): <resumo curto>

<corpo opcional mais longo — o "porquê", não só o "o quê">
```

Use o mesmo vocabulário de `<tipo>` dos prefixos de branch acima (`feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `style`). O escopo geralmente corresponde aos módulos em português de [Contexo](Contexo.md) §6 (`identidade`, `sessao`, `progressao`, `social`, `anotacao` etc.) ou a uma área transversal. ABERTO-11 registra a preferência final do time.

Exemplos, ancorados no domínio deste próprio projeto:

```
feat(sessao): valida conclusao pelo servidor (AD-2)

fix(progression): correct XP formula rounding for co-op bonus

docs(domain-model): resolve D-8 — annotations are row-level private

refactor(frontend): extract deriveStage into its own pure module

test(progression): add boundary tests for stage thresholds (0.199 vs 0.200)

ci(ci): compila backend sem executar testes (T-7)
```

Uma boa descrição explica *por que* uma mudança foi feita, quando isso não é óbvio pelo diff — "corrige off-by-one" está ótimo para um erro de digitação; "corrige o reset de streak à meia-noite porque D-7 exige avaliação no dia local" é muito mais útil daqui a seis meses.

> **Nota sobre idioma:** os exemplos acima estão em inglês porque é assim que eles aparecem no repositório hoje. Se o time preferir commits em português, essa é uma decisão válida — mas escolham um idioma e o mantenham, porque um histórico misturado é mais difícil de escanear do que qualquer um dos dois sozinho. O que não deve ser traduzido em nenhum caso são os `<tipo>` (`feat`, `fix`, ...), os escopos de módulo e os IDs de checklist, porque ferramentas e referências cruzadas dependem deles.

---

## 5. Diretrizes de pull request

Todo PR — para `dev` ou para `main` — deve responder, seja na descrição ou por ser evidente pelo diff:

- **O quê** isso muda, em uma ou duas frases?
- **Por quê** — qual problema ou item de checklist isso endereça?
- **Como foi testado?** Testes unitários, passos manuais, ou ambos.
- **Toca alguma decisão registrada na documentação?** Se sim, qual documento, e ele está atualizado neste mesmo PR?

Um template mínimo de PR (`.github/PULL_REQUEST_TEMPLATE.md`) ajuda isso a acontecer automaticamente em vez de depender da memória — vale configurar cedo.

---

## 6. Expectativas do pipeline de CI

O workflow `ci.yml` verifica o build em PRs para `dev` e `main` com `./mvnw -DskipTests verify`. O workflow `pr-check.yml` valida o fluxo das branches, o título e a seção “Como foi testado”. Registre no PR os comandos ou passos de verificação executados e os resultados. Conforme Q-4, a suíte passa a ser obrigatória quando sua execução na CI for reproduzível (#76).

## 7. Boas práticas

- **Escreva Conventional Commits com uma descrição de verdade.** O público é o seu eu futuro e seus colegas, não o compilador.
- **Atualize a documentação no mesmo PR da mudança**, sempre que a mudança for uma decisão, e não apenas um detalhe de implementação. Se você resolver um item do checklist (05), mexer em um fluxo de dados (02) ou mudar uma regra de domínio (03), o documento deve dizer isso até o PR ser mergeado — não "depois".
- **Aplique Q-4 ao trabalhar com IA.** A execução de testes relevantes é permitida. Para escrever ou alterar testes, siga [Contexo](Contexo.md) §10.1.
- **Seja deliberado sobre o quanto a IA escreve por você.** Este projeto existe para que o time aprenda a construir e raciocinar sobre uma aplicação full-stack — as decisões de arquitetura, os trade-offs de schema, os momentos de "por que esse bug acontece". Apoiar-se em IA para gerar grandes blocos de código não revisado troca esse aprendizado por velocidade de curto prazo. Use para explicar, para revisar, para testar, para destravar — e garanta que você consegue explicar, sem ser perguntado, o que o seu próprio código faz e por que ele tem a forma que tem.

---

## Referência rápida

```bash
# Começar um trabalho novo
git checkout dev && git pull origin dev
git checkout -b feat/minha-feature

# Manter-se atualizado com a dev no meio da branch
git fetch origin && git rebase origin/dev

# Commit
git commit -m "feat(sessao): aplica limite de pausa (D-2)"

# Push e abrir um PR para dev via GitHub

# Depois do merge, limpar localmente
git checkout dev && git pull origin dev
git branch -d feat/20-dominio-sessao
```
