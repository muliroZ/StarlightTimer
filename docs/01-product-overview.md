# 01 — Product Overview

## Vision

StarlightTimer is a Pomodoro application that treats a focus session as something you *build* rather than something you *endure*. A 25-minute cycle is rendered as the life cycle of a star: it condenses out of a cold nebula, ignites, burns steadily, swells, and detonates. Finishing the cycle produces a star. Stars accumulate into a personal sky.

The emotional target is **cozy, not clinical**. Deep blue-black space, supernova accents, translucent surfaces, slow ambient motion. The app should feel like a quiet observatory at night, not a productivity dashboard.

## What makes it different

Most Pomodoro tools are timers with statistics bolted on. StarlightTimer inverts the emphasis:

1. **The timer is the aesthetic centrepiece.** Progress is communicated by stellar evolution, not a shrinking bar.
2. **Progression is long-horizon.** Levels, ranks, badges, and constellations reward months of use, not single sessions.
3. **Focus is social but small.** Co-op rooms are friends-only. There is no public feed, no strangers, no global leaderboard. Competition is scoped to people you actually know.

## Personas

| Persona | Need | What they use |
|---|---|---|
| **The solo student** | Structure for long study blocks, a sense of accumulation | Timer, session history, private annotations, badges |
| **The remote worker** | Ambient co-presence, gentle accountability | Co-op rooms, reminders, music integration |
| **The completionist** | Visible mastery and collection | Badges, constellations, ranks, screen customization |
| **The friend group** | Light rivalry, shared study sessions | Friend ranking, co-op rooms, network hub |

These are working assumptions, not validated research. See checklist item **P-2**.

## Feature catalogue

Grouped by domain area rather than by screen. "Tier" is a recommendation, not a decision — see checklist item **P-1**.

### Core timer
| Feature | Description | Tier |
|---|---|---|
| Focus cycle | Configurable focus duration (default 25 min) with star-lifecycle visualisation | MVP |
| Break cycle | Short break (default 5 min), optional auto-start | MVP |
| Session persistence | A session survives refresh, tab close, and device switch | MVP |
| Long breaks | Longer break after N cycles (classic Pomodoro rule) | Post-MVP |

### Progression & gamification
| Feature | Description | Tier |
|---|---|---|
| XP and levels | Earn XP per completed cycle, level up on a curve | MVP |
| Badges | Discrete achievements, some cumulative ("Supernova ×50"), some conditional ("Night Watch") | MVP |
| Ranks | Named tiers above level ("Stellar Cartographer" → "Quasar") | MVP |
| Progression tracks | Multi-goal progress bars (weekly cycles, hours, break discipline) | Post-MVP |
| Constellations | Grouped goals that complete into a named constellation | Post-MVP |
| Screen customization | Cosmetic themes and backgrounds unlocked by progression | Post-MVP |

### Social
| Feature | Description | Tier |
|---|---|---|
| Friendships | Request / accept / block, friends-only visibility | MVP |
| Users Network Hub | Directory of friends, presence, recent activity | Post-MVP |
| Friend ranking | Leaderboard scoped to the friend graph, periodic reset | Post-MVP |
| Co-op timer rooms | Friends share a synchronised timer in real time | Post-MVP (highest complexity — see doc 02) |

### Personal tooling
| Feature | Description | Tier |
|---|---|---|
| Session history | Browsable log of past sessions with aggregate stats | MVP |
| Private annotations | Notes attached to a session or a day, never visible to others | MVP |
| Customizable reminders | Nudges to start, to take a break, to return after drifting | Post-MVP |
| Music integration | Built-in ambience loops and/or external streaming provider | Post-MVP (licensing risk — see checklist **L-1**) |

## Recommended MVP boundary

Ship the smallest thing that still feels like StarlightTimer rather than a generic timer:

> **Authentication + solo focus/break timer with the full star lifecycle + session persistence + session history + private annotations + XP, levels, and badges + friendships.**

Deliberately excluded from MVP: co-op rooms, music, reminders, cosmetics, leaderboards.

The reasoning is that the star lifecycle and the progression loop are the product's identity, and both are achievable with a plain request/response backend. Co-op rooms introduce real-time infrastructure, a second consistency model, and a large class of edge cases; adding them before the core loop is validated risks spending most of the team's budget on the feature with the least certain payoff.

## Domain glossary

The theme is not decoration — it's the ubiquitous language. Use these terms in code, database columns, tickets, and UI copy so that conversation, schema, and interface stay aligned.

| Term | Meaning | Neutral equivalent |
|---|---|---|
| **Star** | One successfully completed focus cycle | Completed pomodoro |
| **Stars forged** | Lifetime count of completed focus cycles | Total completed sessions |
| **Ignite** | Start a focus cycle | Start timer |
| **Cycle** | One focus or break period | Interval |
| **Stage** | One of the six visual phases of a cycle | Progress phase |
| **Orbit** | A continuous run of active days | Streak |
| **In orbit** | Cumulative focus time | Total focus hours |
| **Constellation** | A named group of related goals | Achievement set |
| **Rank** | Named tier derived from level | Tier / league |
| **Star Log** | The session history view | History |
| **Ambience** | Music and sound settings | Audio settings |
| **Supernova alert** | Cycle-completion sound | Completion chime |

### The six stages

Taken directly from the prototype logic. Stage is a pure function of elapsed percentage — it is **derived on the client and never stored**.

| Stage | Elapsed | Accent | Narrative role |
|---|---|---|---|
| Nebula | 0–20% | `#8fa8ff` | Settling in |
| Protostar | 20–40% | `#a98bff` | Warming up |
| Main Sequence | 40–68% | `#ffe08a` | Deep work |
| Red Giant | 68–88% | `#ff9a7a` | Push through |
| Supernova | 88–100% | `#ff7ad9` | Finish the thought |
| Cooling Nebula | break cycles | `#7fd8ff` | Rest |

One consequence worth noting early: because stage is derived from percentage rather than absolute minutes, a user who configures a 50-minute cycle gets the same narrative arc stretched over twice the time. That is probably the desired behaviour, but it should be an explicit decision — see checklist **D-4**.
