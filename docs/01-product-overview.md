# 01 — Visão Geral do Produto

## Visão

StarlightTimer é uma aplicação Pomodoro que trata uma sessão de foco como algo que você *constrói*, e não algo que você *suporta*. Um ciclo de 25 minutos é renderizado como o ciclo de vida de uma estrela: ela se condensa a partir de uma nebulosa fria, entra em ignição, queima de forma estável, incha e detona. Terminar o ciclo produz uma estrela. As estrelas se acumulam em um céu pessoal.

O alvo emocional é **aconchegante, não clínico**. Espaço em azul-preto profundo, acentos de supernova, superfícies translúcidas, movimento ambiente lento. O app deve parecer um observatório silencioso à noite, não um dashboard de produtividade.

## O que o diferencia

A maioria das ferramentas de Pomodoro é um timer com estatísticas parafusadas em cima. O StarlightTimer inverte a ênfase:

1. **O timer é a peça central estética.** O progresso é comunicado por evolução estelar, não por uma barra que encolhe.
2. **A progressão é de longo prazo.** Níveis, ranks, badges e constelações recompensam meses de uso, não sessões isoladas.
3. **O foco é social, mas pequeno.** Salas co-op são apenas entre amigos. Não há feed público, não há estranhos, não há leaderboard global. A competição se limita a pessoas que você realmente conhece.

## Personas

| Persona | Necessidade | O que usa |
|---|---|---|
| **O estudante solo** | Estrutura para blocos longos de estudo, sensação de acúmulo | Timer, histórico de sessões, anotações privadas, badges |
| **O trabalhador remoto** | Co-presença ambiente, cobrança gentil | Salas co-op, lembretes, integração com música |
| **O colecionador** | Maestria visível e coleção | Badges, constelações, ranks, customização de tela |
| **O grupo de amigos** | Rivalidade leve, sessões de estudo compartilhadas | Ranking de amigos, salas co-op, hub de rede |

Estas são hipóteses de trabalho, não pesquisa validada. Veja o item **P-2** do checklist.

## Catálogo de features

Agrupadas por área de domínio, e não por tela. "Tier" é uma recomendação, não uma decisão — veja o item **P-1** do checklist.

### Timer principal
| Feature | Descrição | Tier |
|---|---|---|
| Ciclo de foco | Duração de foco configurável (padrão 25 min) com visualização do ciclo de vida da estrela | MVP |
| Ciclo de pausa | Pausa curta (padrão 5 min), início automático opcional | MVP |
| Persistência da sessão | Uma sessão sobrevive a refresh, fechamento de aba e troca de dispositivo | MVP |
| Pausas longas | Pausa mais longa a cada N ciclos (regra clássica do Pomodoro) | Pós-MVP |

### Progressão & gamificação
| Feature | Descrição | Tier |
|---|---|---|
| XP e níveis | Ganha XP por ciclo concluído, sobe de nível em uma curva | MVP |
| Badges | Conquistas discretas, algumas cumulativas ("Supernova ×50"), algumas condicionais ("Night Watch") | MVP |
| Ranks | Tiers nomeados acima do nível ("Stellar Cartographer" → "Quasar") | MVP |
| Trilhas de progressão | Barras de progresso de múltiplas metas (ciclos semanais, horas, disciplina de pausa) | Pós-MVP |
| Constelações | Metas agrupadas que se completam em uma constelação nomeada | Pós-MVP |
| Customização de tela | Temas e fundos cosméticos desbloqueados por progressão | Pós-MVP |

### Social
| Feature | Descrição | Tier |
|---|---|---|
| Amizades | Solicitar / aceitar / bloquear, visibilidade só entre amigos | MVP |
| Users Network Hub | Diretório de amigos, presença, atividade recente | Pós-MVP |
| Ranking de amigos | Leaderboard limitado ao grafo de amizades, com reset periódico | Pós-MVP |
| Salas de timer co-op | Amigos compartilham um timer sincronizado em tempo real | Pós-MVP (maior complexidade — veja doc 02) |

### Ferramentas pessoais
| Feature | Descrição | Tier |
|---|---|---|
| Histórico de sessões | Log navegável de sessões passadas com estatísticas agregadas | MVP |
| Anotações privadas | Notas anexadas a uma sessão ou a um dia, nunca visíveis a terceiros | MVP |
| Lembretes customizáveis | Cutucadas para começar, para pausar, para voltar depois de se dispersar | Pós-MVP |
| Integração com música | Loops de ambiência embutidos e/ou provedor externo de streaming | Pós-MVP (risco de licenciamento — veja **L-1** no checklist) |

## Limite de MVP recomendado

Entregue a menor coisa que ainda pareça StarlightTimer, e não um timer genérico:

> **Autenticação + timer solo de foco/pausa com o ciclo de vida completo da estrela + persistência da sessão + histórico de sessões + anotações privadas + XP, níveis e badges + amizades.**

Deliberadamente fora do MVP: salas co-op, música, lembretes, cosméticos, leaderboards.

O raciocínio é que o ciclo de vida da estrela e o loop de progressão são a identidade do produto, e ambos são alcançáveis com um backend simples de request/response. Salas co-op introduzem infraestrutura de tempo real, um segundo modelo de consistência e uma classe grande de casos extremos; adicioná-las antes de validar o loop principal arrisca gastar a maior parte do orçamento do time na feature com o retorno menos garantido.

## Glossário de domínio

O tema não é decoração — é a linguagem ubíqua. Use estes termos em código, colunas de banco, tickets e textos de UI para que conversa, schema e interface permaneçam alinhados.

Os termos são mantidos em inglês porque é assim que eles vão aparecer no código.

| Termo | Significado | Equivalente neutro |
|---|---|---|
| **Star** | Um ciclo de foco concluído com sucesso | Pomodoro concluído |
| **Stars forged** | Contagem vitalícia de ciclos de foco concluídos | Total de sessões concluídas |
| **Ignite** | Iniciar um ciclo de foco | Iniciar o timer |
| **Cycle** | Um período de foco ou de pausa | Intervalo |
| **Stage** | Uma das seis fases visuais de um ciclo | Fase de progresso |
| **Orbit** | Uma sequência contínua de dias ativos | Streak |
| **In orbit** | Tempo de foco acumulado | Total de horas de foco |
| **Constellation** | Um grupo nomeado de metas relacionadas | Conjunto de conquistas |
| **Rank** | Tier nomeado derivado do nível | Tier / liga |
| **Star Log** | A tela de histórico de sessões | Histórico |
| **Ambience** | Configurações de música e som | Configurações de áudio |
| **Supernova alert** | Som de conclusão de ciclo | Sinal sonoro de término |

### Os seis estágios

Retirados diretamente da lógica do protótipo. O estágio é uma função pura do percentual decorrido — ele é **derivado no cliente e nunca armazenado**.

| Estágio | Decorrido | Acento | Papel narrativo |
|---|---|---|---|
| Nebula | 0–20% | `#8fa8ff` | Se acomodando |
| Protostar | 20–40% | `#a98bff` | Esquentando |
| Main Sequence | 40–68% | `#ffe08a` | Trabalho profundo |
| Red Giant | 68–88% | `#ff9a7a` | Aguente firme |
| Supernova | 88–100% | `#ff7ad9` | Termine o raciocínio |
| Cooling Nebula | ciclos de pausa | `#7fd8ff` | Descanso |

Uma consequência que vale notar desde já: como o estágio deriva do percentual e não de minutos absolutos, um usuário que configura um ciclo de 50 minutos recebe o mesmo arco narrativo esticado pelo dobro do tempo. Esse é provavelmente o comportamento desejado, mas deve ser uma decisão explícita — veja **D-4** no checklist.
