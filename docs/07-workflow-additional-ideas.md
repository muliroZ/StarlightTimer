# 07 — Ideias Adicionais de Workflow & Gestão (Opcional)

Ideias opcionais de processo. As regras vigentes de branches, PRs, idioma do código e testes estão em [Contexo](Contexo.md) §9–10 e [06-git-workflow](06-git-workflow.md); esta nota não as substitui. A API segue hexagonal por feature e há workers externos na topologia.

---

## 1. Automação local, para que a convenção se imponha sozinha

Conventional Commits (doc 06 §4) são fáceis de esquecer sob pressão de prazo. Duas ferramentas leves eliminam a necessidade de lembrar:

- **commitlint + Husky** — um hook do Git rejeita um commit cuja mensagem não segue o formato Conventional Commits, *antes* que ela chegue a um PR.
- **lint-staged** — roda o linter/formatador apenas nos arquivos em stage, a cada commit, para que implicâncias de estilo nunca cheguem ao review.

Custo: alguns minutos de configuração, uma vez. Benefício: ninguém precisa ser "a pessoa que conserta mensagens de commit no review".

## 2. Template de PR e CODEOWNERS

- O repositório já tem `.github/pull_request_template.md`; mantenham suas seções em vez de criar um segundo template.
- Um arquivo `CODEOWNERS` mapeando módulos de backend vigentes (`identidade`, `sessao`, `progressao`, `social`, ...) para a pessoa que é dona daquela área (conforme **Q-5** do checklist) solicita automaticamente o revisor certo, em vez de depender de alguém lembrar de marcá-lo.

## 3. Estratégia de merge: escolham uma e escrevam

O guia recomenda squash-merge para a `dev` e não prescreve nada para `dev` → `main`. Vale decidir explicitamente:

| Estratégia | Efeito | Boa para |
|---|---|---|
| Squash merge | Um commit por feature/correção | `feat/* → dev` — mantém a `dev` legível |
| Merge commit (sem fast-forward) | Preserva o histórico interno do módulo | `dev → main` — permite ver como um módulo foi construído, não apenas que ele existe |

Inconsistência aqui não é perigosa, só confusa — qualquer que seja a escolha, registrem no doc 06 assim que decidirem.

## 4. Versionamento atrelado aos módulos de desenvolvimento

Como a `main` só se move quando um módulo de desenvolvimento é concluído, marcar esse commit de merge com uma versão (`v0.1.0`, `v0.2.0`...) transforma o limite de MVP do doc 01 em algo visível no `git log`, e não apenas em um arquivo markdown. Se quiserem isso totalmente automático, o `semantic-release` consegue derivar o incremento de versão a partir dos tipos de commit (`feat` → minor, `fix` → patch, rodapé `BREAKING CHANGE` → major) — provavelmente mais cerimônia do que um projeto de faculdade precisa, mas vale saber que existe.

## 5. Architecture Decision Records (ADRs)

O doc 02 já numera suas decisões (**AD-1** a **AD-6**). Estender isso para uma pasta `docs/adr/`, um arquivo por decisão, com um formato fixo (contexto → decisão → consequências → status), dá às decisões futuras o mesmo tratamento sem inchar o doc 02 em si. Útil a partir do momento em que o time começar a tomar decisões *depois* do planejamento inicial — que é exatamente quando o doc 02 deixa de ser o lugar natural para registrá-las.

## 6. Um board de projeto espelhando o checklist

O doc 05 agrupa decisões resolvidas e abertas por prefixo (`P`, `D`, `R`, `A`, `L`, `N`, `T`, `F`, `Q`). Espelhar essa estrutura como labels ou colunas no GitHub Projects transforma o checklist de um documento que você relê em um board a partir do qual você de fato trabalha — cada card pode referenciar seu ID de checklist (`A-2`, `F-3`) diretamente no título, e a rastreabilidade sai de graça.

## 7. Rastreabilidade da branch até a decisão

Onde for prático, referenciem o ID do checklist ou o número da issue tanto no nome da branch quanto no commit — `fix/12-d7-streak-timezone` — para que qualquer pessoa lendo o `git log` depois consiga rastrear uma mudança até o *porquê* sem depender de memória tribal.

## 8. Definition of Done, tornada concreta

O item **Q-1** do checklist pede isso de forma abstrata. Um ponto de partida concreto para este projeto especificamente:

- [ ] Comandos ou passos de verificação e resultados registrados no PR, conforme Q-4 em [Contexo](Contexo.md) §10.1
- [ ] Migration revisada, se o schema mudou (**AD-4**)
- [ ] Documentação relevante atualizada no mesmo PR, se uma decisão foi tomada ou alterada
- [ ] CI de compilação verde quando BL-06 estiver concluído, usando `./mvnw -DskipTests verify`
- [ ] Ao menos um review, com os comentários resolvidos (não apenas descartados)

## 9. Distribuir a carga de review em um time pequeno

Em um time pequeno, as mesmas uma ou duas pessoas acabam revisando tudo, o que ao mesmo tempo cria gargalo nos merges e faz com que menos gente do time efetivamente leia a base de código inteira. Rotacionar quem revisa qual módulo (amarrem isso à propriedade de **Q-5**, mas não deixem propriedade virar "só esta pessoa revisa esta área") mantém o conhecimento — e a fila de review — distribuídos.

## 10. Mantenham as branches de vida curta

Uma branch `feat/*` que vive três semanas está, por definição, divergindo da `dev` há três semanas — o rebase do doc 06 §3 fica doloroso na proporção direta de quanto tempo a branch sobreviveu. Se uma feature é grande o bastante para levar tanto tempo, isso geralmente é sinal de que ela deveria ser dividida em branches menores (primeiro um esqueleto, depois o comportamento), em vez de mantida como uma única branch de longa duração.
