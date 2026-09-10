# 04 — Frontend & Design System

Tokens below are extracted directly from the Claude Design prototype (`StarlightTimer.dc.html`). They are the visual contract — implement them as CSS custom properties or theme constants before building components, so nobody hard-codes a hex value.

## Design tokens

### Surfaces

```css
--slt-void:        #04060f;  /* page base */
--slt-deep-1:      #050a1c;  /* gradient stop, top */
--slt-deep-2:      #050817;  /* gradient stop, mid */
--slt-deep-3:      #03050e;  /* gradient stop, bottom */

--slt-panel:       rgba(9, 16, 40, .55);    /* cards, profile sections */
--slt-panel-alt:   rgba(10, 18, 44, .45);   /* stat tiles, mode pill */
--slt-header:      rgba(8, 14, 36, .45);    /* top bar, with blur(10px) */
--slt-control:     rgba(120, 150, 255, .07); /* sidebar buttons, inactive nav */
--slt-control-hi:  rgba(140, 170, 255, .16); /* hover */
--slt-nav-active:  rgba(140, 175, 255, .22);
```

The page background is a three-layer composite: two radial gradients (violet at 78% -8%, cyan at 8% 92%) over a vertical linear gradient, with a separately animated star field on top. Build it once as a `<SpaceBackdrop>` and never repeat it.

### Text

```css
--slt-text:        #e9edff;  /* body */
--slt-text-bright: #f4f7ff;  /* timer digits, headings */
--slt-text-muted:  rgba(206, 218, 255, .62);
--slt-text-faint:  rgba(190, 205, 255, .45);  /* section eyebrows */
--slt-link:        #7fb2ff;
--slt-link-hover:  #b9d4ff;
```

### Stage accents

The lifecycle palette. Each stage has a ring colour, a radial-gradient core, a halo, a core diameter, and a glow radius.

| Stage | Ring | Halo | Core Ø | Glow |
|---|---|---|---|---|
| Nebula | `#8fa8ff` | `rgba(130,150,255,.40)` | 44px | 34 |
| Protostar | `#a98bff` | `rgba(170,130,255,.45)` | 58px | 48 |
| Main Sequence | `#ffe08a` | `rgba(255,205,120,.50)` | 74px | 62 |
| Red Giant | `#ff9a7a` | `rgba(255,130,100,.50)` | 92px | 70 |
| Supernova | `#ff7ad9` | `rgba(255,140,230,.60)` | 104px | 96 |
| Cooling Nebula | `#7fd8ff` | `rgba(110,200,255,.45)` | 56px | 44 |

The growing core diameter is doing real narrative work — the star visibly swells from 44px to 104px across the cycle. Preserve that.

### Borders and radii

```css
--slt-border:       rgba(150, 180, 255, .18);
--slt-border-hi:    rgba(180, 205, 255, .50);
--slt-border-warm:  rgba(255, 210, 150, .55);  /* avatar */

--slt-radius-pill:  999px;   /* all buttons, badges, progress bars */
--slt-radius-card:  18px;
--slt-radius-hero:  22px;
--slt-radius-tile:  12px–14px;
```

### Typography

Two families, loaded from Google Fonts.

| Family | Weights | Used for |
|---|---|---|
| **Space Grotesk** | 300, 400, 500, 700 | UI text, headings, button labels, body copy |
| **IBM Plex Mono** | 400, 500 | Timer digits, numeric stats, eyebrow labels, stage names |

The split is consistent and worth enforcing: **anything numeric or label-like is mono; anything readable is Grotesk.**

Recurring type patterns:

```css
/* Eyebrow — section labels, "NAVIGATION", "TONIGHT" */
font: 400 9.5px 'IBM Plex Mono'; letter-spacing: .24em; text-transform: uppercase;

/* Wordmark */
font: 300 21px 'Space Grotesk'; letter-spacing: .34em; text-transform: uppercase;
/* "Timer" is weight 700 inside the same element */

/* Timer digits */
font: 300 52px 'IBM Plex Mono'; letter-spacing: .04em;

/* Big stat numbers */
font: 300 30px 'IBM Plex Mono';

/* Button label */
font: 500 13px 'Space Grotesk'; letter-spacing: .14em; text-transform: uppercase;
```

### Motion

```css
@keyframes slt-twinkle { /* 5.5s — star field opacity .35 ↔ .95 */ }
@keyframes slt-pulse   { /* 3.4s — star core scale 1 ↔ 1.06 */ }
@keyframes slt-halo    { /* 6–7s — halo scale 1 ↔ 1.18, opacity .5 ↔ .15 */ }
@keyframes slt-spin    { /* rotation, unused in current screens */ }
```

All four are infinite. **All four must be disabled under `prefers-reduced-motion`** — three simultaneous infinite animations on a page a user stares at for 25 minutes is a genuine accessibility and comfort problem, not a hypothetical one. See checklist **F-4**.

The progress ring transitions `stroke-dashoffset` over `.9s linear`, which is what makes it glide rather than tick.

### The opacity system

The prototype uses opacity as its primary hierarchy device: primary actions sit at `.90`, secondary at `.78`, tertiary at `.60–.72`, and everything rises to `1` on hover. This is a large part of why the UI reads as "cozy" rather than "app-like," and it should be kept.

It is also a **contrast risk**. A `.60`-opacity light-grey label on a near-black background may fall below WCAG AA (4.5:1 for body text). This needs measuring rather than guessing — see checklist **F-3**. The likely resolution is raising the floor from `.60` to somewhere around `.75` for anything carrying text, while keeping low opacity on decorative borders and fills where contrast rules don't apply.

## Screen inventory

| Screen | Prototype | Notes |
|---|---|---|
| Homepage / Timer | ✅ Built | Three-column: nav sidebar, timer, stats sidebar |
| Profile | ✅ Built | Hero + badges grid + progression + settings |
| Sign in / Sign up | ❌ Missing | Needed for MVP |
| Star Log (session history) | ❌ Missing | Sidebar links to it; needed for MVP |
| Settings (full page) | ❌ Missing | Profile has a partial panel only |
| Co-op room | ❌ Missing | Post-MVP |
| Users Network Hub | ❌ Missing | Post-MVP |
| Friend ranking | ❌ Missing | Post-MVP |
| Constellations | ❌ Missing | Sidebar links to it |
| Ambience / music | ❌ Missing | Sidebar links to it |
| Orbit Goals | ❌ Missing | Sidebar links to it |
| Empty states | ❌ Missing | Day one has no stars, no badges, no friends |
| Error / offline states | ❌ Missing | |

Four sidebar buttons in the prototype link to screens that don't exist yet. That's fine for a mockup, but it's four unscoped features hiding behind plausible-looking navigation.

**The empty-state gap is worth flagging separately.** The Profile screen is designed for a level-12 user with 218 stars and a 31-day streak. A brand-new user sees zeros, six locked badges, and empty progress bars — which is the least motivating possible first impression of a motivational app. Design the day-one state explicitly.

## Component decomposition

```
<AppShell>
├── <SpaceBackdrop>            ← gradient composite + animated star field
├── <TopBar>
│   ├── <NavPills>             ← Home / Profile / Settings
│   ├── <Wordmark>             ← "StarlightTimer" + tagline
│   └── <UserChip>             ← name, LV · rank, <Avatar>
└── <main>

Timer screen
├── <SidebarNav>               ← Star Log, Constellations, Ambience, Orbit Goals
├── <TimerPanel>
│   ├── <ModePill>             ← "focus cycle · 25 min"
│   ├── <StellarTimer>         ← halo + SVG ring + core + digits + stage name
│   ├── <StageNarrative>
│   └── <TimerControls>        ← Ignite/Pause/Resume, Break, Reset
└── <TonightPanel>             ← <StatTile> ×2 + history link

Profile screen
├── <ProfileHero>              ← <Avatar> lg, rank chip, <XpBar>, <StatTile> ×3
├── <BadgeGrid>                ← <BadgeMedal> ×n
├── <ProgressionPanel>         ← <TrackBar> ×n
└── <SettingsPanel>            ← <SettingToggle> ×n + actions
```

### `<StellarTimer>` — the one component that matters

Everything else is conventional. This one carries the product identity and deserves care.

**Inputs:** `elapsedSeconds`, `totalSeconds`, `mode` (`focus | break`).

**Derives:** progress fraction → stage → ring colour, core gradient, halo colour, core diameter, glow radius, `stroke-dashoffset` (circumference 578 for r=92), and the `MM:SS` label.

**Contract:** the stage function is pure and stateless. Extract it to `deriveStage(progress, mode)` in its own module and unit-test it at the boundaries (0.199 vs 0.200, 0.679 vs 0.680, etc.). It's a lookup table, but it's the lookup table that defines the product, and boundary bugs there are the kind that ship.

**Countdown source:** per **AD-2**, do not count down from a locally stored number. Compute `elapsedSeconds` from the server-provided `startedAt` and the current time on every tick. Browsers throttle `setInterval` in background tabs; a locally decremented counter will drift, and a user who switches tabs for ten minutes will come back to a timer that's wrong. Deriving from timestamps makes throttling harmless — the next tick simply computes the correct value.

## Frontend architecture notes

Kept short deliberately — most of this is team preference, and it's tracked in the checklist.

- **Folder structure:** feature-first (`features/timer/`, `features/profile/`) mirroring the backend module map, with `components/ui/` for shared primitives. Mirrored structure makes cross-stack navigation cheap.
- **Server state vs client state:** these are genuinely different problems. Session history and profile data are server state (caching, refetch, staleness). Current timer position and UI toggles are client state. Using one tool for both is the usual source of frontend mess.
- **The timer's position is not React state.** Storing a seconds counter in `useState` and decrementing it re-renders the whole tree every second. Derive from timestamps, keep the tick in a single component, and let the rest of the app subscribe only to what changes.
- **Theming:** screen customization is a planned feature, so define the token layer as CSS custom properties on `:root` from day one. Swapping a theme should be swapping a variable set, not touching components.
