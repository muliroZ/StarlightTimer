# 04 — Frontend & Design System

Os tokens abaixo foram extraídos diretamente do protótipo do Claude Design (`StarlightTimer.dc.html`). Eles são o contrato visual — implementem-nos como CSS custom properties ou constantes de tema antes de construir componentes, para que ninguém escreva um hex na mão.

## Design tokens

### Superfícies

```css
--slt-void:        #04060f;  /* base da página */
--slt-deep-1:      #050a1c;  /* parada do gradiente, topo */
--slt-deep-2:      #050817;  /* parada do gradiente, meio */
--slt-deep-3:      #03050e;  /* parada do gradiente, base */

--slt-panel:       rgba(9, 16, 40, .55);    /* cards, seções do perfil */
--slt-panel-alt:   rgba(10, 18, 44, .45);   /* tiles de estatística, pill de modo */
--slt-header:      rgba(8, 14, 36, .45);    /* barra superior, com blur(10px) */
--slt-control:     rgba(120, 150, 255, .07); /* botões da sidebar, nav inativa */
--slt-control-hi:  rgba(140, 170, 255, .16); /* hover */
--slt-nav-active:  rgba(140, 175, 255, .22);
```

O fundo da página é um composto de três camadas: dois gradientes radiais (violeta em 78% -8%, ciano em 8% 92%) sobre um gradiente linear vertical, com um campo de estrelas animado separadamente por cima. Construam isso uma vez como `<SpaceBackdrop>` e nunca repitam.

### Texto

```css
--slt-text:        #e9edff;  /* corpo */
--slt-text-bright: #f4f7ff;  /* dígitos do timer, títulos */
--slt-text-muted:  rgba(206, 218, 255, .62);
--slt-text-faint:  rgba(190, 205, 255, .45);  /* eyebrows de seção */
--slt-link:        #7fb2ff;
--slt-link-hover:  #b9d4ff;
```

### Acentos por estágio

A paleta do ciclo de vida. Cada estágio tem uma cor de anel, um núcleo em gradiente radial, um halo, um diâmetro de núcleo e um raio de brilho.

| Estágio | Anel | Halo | Ø do núcleo | Brilho |
|---|---|---|---|---|
| Nebula | `#8fa8ff` | `rgba(130,150,255,.40)` | 44px | 34 |
| Protostar | `#a98bff` | `rgba(170,130,255,.45)` | 58px | 48 |
| Main Sequence | `#ffe08a` | `rgba(255,205,120,.50)` | 74px | 62 |
| Red Giant | `#ff9a7a` | `rgba(255,130,100,.50)` | 92px | 70 |
| Supernova | `#ff7ad9` | `rgba(255,140,230,.60)` | 104px | 96 |
| Cooling Nebula | `#7fd8ff` | `rgba(110,200,255,.45)` | 56px | 44 |

O diâmetro crescente do núcleo está fazendo trabalho narrativo de verdade — a estrela visivelmente incha de 44px para 104px ao longo do ciclo. Preservem isso.

### Bordas e raios

```css
--slt-border:       rgba(150, 180, 255, .18);
--slt-border-hi:    rgba(180, 205, 255, .50);
--slt-border-warm:  rgba(255, 210, 150, .55);  /* avatar */

--slt-radius-pill:  999px;   /* todos os botões, badges, barras de progresso */
--slt-radius-card:  18px;
--slt-radius-hero:  22px;
--slt-radius-tile:  12px–14px;
```

### Tipografia

Duas famílias, carregadas do Google Fonts.

| Família | Pesos | Usada para |
|---|---|---|
| **Space Grotesk** | 300, 400, 500, 700 | Texto de UI, títulos, rótulos de botão, texto corrido |
| **IBM Plex Mono** | 400, 500 | Dígitos do timer, estatísticas numéricas, rótulos eyebrow, nomes de estágio |

A divisão é consistente e vale ser imposta: **tudo que é numérico ou parece rótulo é mono; tudo que é legível é Grotesk.**

Padrões de tipo recorrentes:

```css
/* Eyebrow — rótulos de seção, "NAVIGATION", "TONIGHT" */
font: 400 9.5px 'IBM Plex Mono'; letter-spacing: .24em; text-transform: uppercase;

/* Wordmark */
font: 300 21px 'Space Grotesk'; letter-spacing: .34em; text-transform: uppercase;
/* "Timer" tem peso 700 dentro do mesmo elemento */

/* Dígitos do timer */
font: 300 52px 'IBM Plex Mono'; letter-spacing: .04em;

/* Números grandes de estatística */
font: 300 30px 'IBM Plex Mono';

/* Rótulo de botão */
font: 500 13px 'Space Grotesk'; letter-spacing: .14em; text-transform: uppercase;
```

### Movimento

```css
@keyframes slt-twinkle { /* 5.5s — opacidade do campo de estrelas .35 ↔ .95 */ }
@keyframes slt-pulse   { /* 3.4s — escala do núcleo da estrela 1 ↔ 1.06 */ }
@keyframes slt-halo    { /* 6–7s — escala do halo 1 ↔ 1.18, opacidade .5 ↔ .15 */ }
@keyframes slt-spin    { /* rotação, não usada nas telas atuais */ }
```

As quatro são infinitas. **As quatro precisam ser desativadas sob `prefers-reduced-motion`** — três animações infinitas simultâneas em uma tela que o usuário encara por 25 minutos é um problema real de acessibilidade e conforto, não hipotético. Veja **F-4** no checklist.

O anel de progresso faz transição de `stroke-dashoffset` em `.9s linear`, que é o que o faz deslizar em vez de dar saltos.

### O sistema de opacidade

O protótipo usa opacidade como principal recurso de hierarquia: ações primárias ficam em `.90`, secundárias em `.78`, terciárias em `.60–.72`, e tudo sobe para `1` no hover. Isso é grande parte do motivo pelo qual a UI é lida como "aconchegante" em vez de "app corporativo", e deve ser mantido.

É também um **risco de contraste**. Um rótulo cinza-claro com opacidade `.60` sobre um fundo quase preto pode ficar abaixo do WCAG AA (4.5:1 para texto de corpo). Isso precisa ser medido, não chutado — veja **F-3** no checklist. A resolução provável é elevar o piso de `.60` para algo em torno de `.75` para qualquer elemento que carregue texto, mantendo a opacidade baixa em bordas e preenchimentos decorativos, onde regras de contraste não se aplicam.

## Inventário de telas

| Tela | Protótipo | Notas |
|---|---|---|
| Homepage / Timer | ✅ Construída | Três colunas: sidebar de navegação, timer, sidebar de estatísticas |
| Perfil | ✅ Construída | Hero + grade de badges + progressão + configurações |
| Login / Cadastro | ❌ Faltando | Necessária para o MVP |
| Star Log (histórico de sessões) | ❌ Faltando | A sidebar linka para ela; necessária para o MVP |
| Configurações (página completa) | ❌ Faltando | O perfil tem apenas um painel parcial |
| Sala co-op | ❌ Faltando | Pós-MVP |
| Users Network Hub | ❌ Faltando | Pós-MVP |
| Ranking de amigos | ❌ Faltando | Pós-MVP |
| Constelações | ❌ Faltando | A sidebar linka para ela |
| Ambiência / música | ❌ Faltando | A sidebar linka para ela |
| Orbit Goals | ❌ Faltando | A sidebar linka para ela |
| Estados vazios | ❌ Faltando | No primeiro dia não há estrelas, badges nem amigos |
| Estados de erro / offline | ❌ Faltando | |

Quatro botões da sidebar no protótipo linkam para telas que ainda não existem. Isso é aceitável em um mockup, mas são quatro features sem escopo escondidas atrás de uma navegação que parece plausível.

**A lacuna de estados vazios merece destaque separado.** A tela de Perfil foi desenhada para um usuário de nível 12 com 218 estrelas e uma sequência de 31 dias. Um usuário novo em folha vê zeros, seis badges bloqueados e barras de progresso vazias — que é a primeira impressão menos motivadora possível para um app motivacional. Projetem o estado do primeiro dia explicitamente.

## Decomposição de componentes

```
<AppShell>
├── <SpaceBackdrop>            ← composto de gradientes + campo de estrelas animado
├── <TopBar>
│   ├── <NavPills>             ← Home / Perfil / Configurações
│   ├── <Wordmark>             ← "StarlightTimer" + tagline
│   └── <UserChip>             ← nome, LV · rank, <Avatar>
└── <main>

Tela do Timer
├── <SidebarNav>               ← Star Log, Constellations, Ambience, Orbit Goals
├── <TimerPanel>
│   ├── <ModePill>             ← "focus cycle · 25 min"
│   ├── <StellarTimer>         ← halo + anel SVG + núcleo + dígitos + nome do estágio
│   ├── <StageNarrative>
│   └── <TimerControls>        ← Ignite/Pause/Resume, Break, Reset
└── <TonightPanel>             ← <StatTile> ×2 + link para histórico

Tela de Perfil
├── <ProfileHero>              ← <Avatar> lg, chip de rank, <XpBar>, <StatTile> ×3
├── <BadgeGrid>                ← <BadgeMedal> ×n
├── <ProgressionPanel>         ← <TrackBar> ×n
└── <SettingsPanel>            ← <SettingToggle> ×n + ações
```

### `<StellarTimer>` — o único componente que realmente importa

Todo o resto é convencional. Este carrega a identidade do produto e merece cuidado.

**Entradas:** `elapsedSeconds`, `totalSeconds`, `mode` (`focus | break`).

**Deriva:** fração de progresso → estágio → cor do anel, gradiente do núcleo, cor do halo, diâmetro do núcleo, raio do brilho, `stroke-dashoffset` (circunferência 578 para r=92), e o rótulo `MM:SS`.

**Contrato:** a função de estágio é pura e sem estado. Extraiam para `deriveStage(progress, mode)` em um módulo próprio e testem unitariamente nas fronteiras (0.199 vs 0.200, 0.679 vs 0.680, etc.). É uma tabela de lookup, mas é a tabela de lookup que define o produto, e bugs de fronteira ali são do tipo que chega em produção.

**Fonte da contagem:** conforme **AD-2**, não façam contagem regressiva a partir de um número armazenado localmente. Calculem `elapsedSeconds` a partir do `startedAt` fornecido pelo servidor e do horário atual, a cada tick. Navegadores limitam `setInterval` em abas em segundo plano; um contador decrementado localmente vai divergir, e um usuário que troca de aba por dez minutos volta para um timer errado. Derivar de timestamps torna o throttling inofensivo — o próximo tick simplesmente calcula o valor correto.

## Notas de arquitetura do frontend

Mantidas curtas de propósito — a maior parte disso é preferência do time, e está rastreada no checklist.

- **Estrutura de pastas:** orientada a features (`features/timer/`, `features/profile/`), espelhando o mapa de módulos do backend, com `components/ui/` para primitivos compartilhados. Estrutura espelhada torna a navegação entre as duas pontas da stack barata.
- **Estado de servidor vs estado de cliente:** são problemas genuinamente diferentes. Histórico de sessões e dados de perfil são estado de servidor (cache, refetch, obsolescência). A posição atual do timer e os toggles de UI são estado de cliente. Usar uma ferramenta só para os dois é a fonte habitual de bagunça no frontend.
- **A posição do timer não é estado do React.** Guardar um contador de segundos em `useState` e decrementá-lo re-renderiza a árvore inteira a cada segundo. Derivem de timestamps, mantenham o tick em um único componente, e deixem o resto do app se inscrever só no que muda.
- **Tematização:** customização de tela é uma feature planejada, então definam a camada de tokens como CSS custom properties em `:root` desde o primeiro dia. Trocar de tema deve ser trocar um conjunto de variáveis, não mexer em componentes.
