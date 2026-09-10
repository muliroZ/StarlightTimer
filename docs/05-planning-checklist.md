# 05 — Planning Checklist

Every decision that is still open. Each item has an ID so it can be referenced from other docs, tickets, and meeting notes.

**Blocking** = something is unbuildable until this is answered.
**Soon** = needed before the relevant feature starts, but not blocking today.
**Later** = can be deferred without cost.

---

## P — Product & scope

- [ ] **P-1** — *Blocking.* Confirm or reject the MVP boundary proposed in doc 01. Everything downstream depends on this line.
- [ ] **P-2** — *Soon.* Validate the personas. Even five conversations with real users beats four assumptions.
- [ ] **P-3** — *Blocking.* Define success. "The app works" is not measurable. Pick two or three: day-7 retention, median cycles per active day, share of started cycles that complete.
- [ ] **P-4** — *Blocking.* Target platforms. Desktop-only? Responsive down to mobile? Installable PWA? The prototype is desktop-first three-column and does not survive a 375px viewport as-is. This changes the frontend estimate substantially.
- [ ] **P-5** — *Soon.* Is there an offline mode? A timer that requires connectivity to start is a timer that fails on hotel Wi-Fi.
- [ ] **P-6** — *Later.* Monetisation, if any. Affects whether cosmetics are earned, bought, or both.
- [ ] **P-7** — *Soon.* Launch scope: is this public, or friends-and-classmates? Determines how much the abuse, moderation, and scaling questions actually matter.

## D — Domain rules

These are the rules nobody has written down, and every one of them is needed before the corresponding code can be written.

- [ ] **D-1** — *Blocking.* **What counts as a completed cycle?** Must the full duration elapse? Is there a grace threshold (e.g. 90% counts)? Does a cycle ended early ever award partial XP?
- [ ] **D-2** — *Blocking.* **Pause semantics.** Can a focus cycle be paused at all? Is there a maximum pause duration before it's auto-abandoned? Does pause time count toward "in orbit" hours? (A cycle paused for two hours and then completed is currently a valid star, which is probably wrong.)
- [ ] **D-3** — *Blocking.* **Clock tolerance.** How many seconds of drift does the server accept between expected and actual duration before rejecting a completion? Needs a number, because browsers throttle background tabs and the drift is real.
- [ ] **D-4** — *Soon.* **Stage scaling.** With a 50-minute cycle, does the star arc stretch proportionally (Main Sequence at 40–68% = minutes 20–34), or are stage boundaries fixed in absolute minutes? Proportional is the current prototype behaviour.
- [ ] **D-5** — *Blocking.* **XP formula.** Flat per cycle? Scaled by duration? Bonus for streaks, for co-op, for completing without pausing? Write the formula down before writing the ledger.
- [ ] **D-6** — *Blocking.* **Level curve and rank thresholds.** How much XP per level, and does it scale? Which named ranks exist and at what levels? The prototype references "Stellar Cartographer" and "Quasar" with no defined tier list.
- [ ] **D-7** — *Blocking.* **Streak definition.** What breaks an orbit — a day with zero completed cycles, or a day with zero *started* cycles? Evaluated in whose timezone? What about someone who works past midnight, which is exactly the "Night Watch" badge persona? Timezone-naive streak logic is one of the most common sources of user-facing bugs in this category of app.
- [ ] **D-8** — *Blocking.* **What "private" means for annotations.** Not-shared-with-friends, or encrypted-from-operators? See doc 03. The answer changes the schema and the cost.
- [ ] **D-9** — *Soon.* **Badge catalogue.** Full list with exact criteria. The prototype shows six of a stated fourteen; the other eight are undefined, and the six shown have no written criteria.
- [ ] **D-10** — *Soon.* **Ranking mechanics.** Lifetime or periodic? If periodic, what period and when does it reset? What's the tiebreaker? Does a friend who joins today start at zero against a friend with six months of history — and if so, is that demotivating enough to matter?
- [ ] **D-11** — *Soon.* **Constellations.** What is one, concretely? A themed group of badges, a group of goals, a visual arrangement of earned stars? The term appears in the UI with no definition behind it.
- [ ] **D-12** — *Later.* **Long breaks.** Does the classic "long break every 4 cycles" rule apply? Configurable?

## R — Co-op rooms

The most under-specified feature and the most expensive. Do not start it until all of these are answered.

- [ ] **R-1** — *Blocking (for the feature).* Shared timer or parallel timers? Does everyone run one synchronised cycle, or does each member run their own while sharing presence? These are entirely different features with entirely different costs.
- [ ] **R-2** — *Blocking (for the feature).* Who can control the timer? Host only, anyone, or majority? What happens on disagreement?
- [ ] **R-3** — *Blocking (for the feature).* Host disconnect behaviour. Does the room pause, transfer host, continue autonomously, or close?
- [ ] **R-4** — *Blocking (for the feature).* Late join. Can someone join mid-cycle? Do they join at the current position, wait for the next cycle, or start their own?
- [ ] **R-5** — *Blocking (for the feature).* Do co-op cycles award XP identically to solo ones? If they award more, they become the optimal grinding strategy and solo use decays. If less, nobody uses rooms.
- [ ] **R-6** — *Soon.* Room capacity, invite mechanism (link, direct invite, friends-can-just-join), and lifetime (ephemeral vs persistent).
- [ ] **R-7** — *Soon.* Is there chat? If yes, that's moderation, persistence, and abuse-reporting scope that isn't currently counted anywhere.
- [ ] **R-8** — *Soon.* Presence granularity — is "Ana is focusing" visible to friends outside a room? That's a privacy decision as much as a feature.

## A — Authentication & security

- [ ] **A-1** — *Blocking.* Token strategy: session cookie, JWT in memory with refresh, or JWT in httpOnly cookie. Affects the SPA, the WebSocket handshake, and CSRF posture.
- [ ] **A-2** — *Blocking.* Registration flow: email + password only, or social login too? Is email verification required before use?
- [ ] **A-3** — *Blocking.* WebSocket authentication. Tokens in query strings get logged by proxies; decide the handshake mechanism explicitly.
- [ ] **A-4** — *Soon.* Password reset flow, and what it does to any user-key-derived encryption if **D-8** lands on the encrypted reading.
- [ ] **A-5** — *Soon.* Rate limiting, especially on session-completion endpoints (the XP faucet) and friend requests (the spam vector).
- [ ] **A-6** — *Soon.* Anti-cheat posture. Server-side validation is in **AD-2**, but decide how much effort is warranted. Friends-only ranking makes this low-stakes; say so out loud so nobody over-engineers it.
- [ ] **A-7** — *Later.* Abuse reporting and blocking, if the app goes public (**P-7**).

## L — Legal & privacy

- [ ] **L-1** — *Blocking, if music ships.* **Music licensing.** You cannot legally stream arbitrary music from your own servers. Realistic options: (a) integrate a provider like Spotify, which requires the user's own premium account and constrains playback control; (b) license or commission original ambient loops; (c) royalty-free/CC-licensed audio with attribution; (d) let users bring their own and just don't handle audio at all. This is a legal question, not a technical one, and it's the single most likely feature to get cut late for reasons nobody anticipated.
- [ ] **L-2** — *Blocking.* Which privacy regime applies (LGPD, GDPR, both)? Determines consent, export, and deletion obligations.
- [ ] **L-3** — *Blocking.* Account deletion semantics. Hard delete or anonymise? What happens to a deleted user's rows in a friend's ranking history, or their membership in a shared room?
- [ ] **L-4** — *Soon.* Data retention. Do sessions persist forever? Annotations?
- [ ] **L-5** — *Soon.* Exact friend visibility matrix. Write down, field by field, what a friend can see: stars forged, streak, current activity, badges, session titles? Annotations must appear in the "never" column.
- [ ] **L-6** — *Soon.* Terms of service and privacy policy. Needed before any public launch.
- [ ] **L-7** — *Later.* Cookie/consent banner, if analytics ship.

## N — Notifications

- [ ] **N-1** — *Soon.* Channels: in-app only, Web Push, email, or a combination.
- [ ] **N-2** — *Soon.* If Web Push: service worker, VAPID keys, and the permission-request flow. Never prompt on first page load — ask when the user enables a reminder.
- [ ] **N-3** — *Soon.* Reminder types and their triggers. "Orbit reminders — nudge me if I drift for 10 min" appears in the prototype, which implies drift detection: how is drift detected server-side when the user has simply closed the tab?
- [ ] **N-4** — *Soon.* Quiet hours and frequency caps. A productivity app that nags at 2am gets uninstalled.
- [ ] **N-5** — *Later.* Notification preferences granularity — per type, or one global switch?

## T — Technical & infrastructure

- [ ] **T-1** — *Blocking.* Repository strategy: monorepo or separate frontend/backend repos. Affects CI, versioning, and how contract changes are coordinated.
- [ ] **T-2** — *Blocking.* Java version and Spring Boot version. Pin them.
- [ ] **T-3** — *Blocking.* Migration tool: Flyway or Liquibase. Doc 02 assumes Flyway; either is fine, but pick one and set `ddl-auto: validate`.
- [ ] **T-4** — *Blocking.* Local development environment. Docker Compose with Postgres is the low-friction default. "It works on my machine" costs more days than setting this up.
- [ ] **T-5** — *Blocking.* Environments: how many, and what's the promotion path?
- [ ] **T-6** — *Soon.* Hosting for app, database, and static frontend. Budget constraints will drive this more than architecture.
- [ ] **T-7** — *Soon.* CI pipeline: what runs on a PR, and what blocks a merge.
- [ ] **T-8** — *Soon.* Secrets management. Not `application.properties` in git.
- [ ] **T-9** — *Soon.* API conventions: URL structure, versioning, pagination, error shape, date format. Write a one-page convention doc *before* the tenth endpoint, not after.
- [ ] **T-10** — *Soon.* OpenAPI generation, and whether frontend types are generated from it. Generated types eliminate an entire class of integration bug for roughly an afternoon of setup.
- [ ] **T-11** — *Soon.* Observability: structured logging, error tracking, uptime monitoring.
- [ ] **T-12** — *Soon.* Backup and restore for Postgres — and confirm that restore has actually been tested.
- [ ] **T-13** — *Later.* Whether Redis is needed. Per doc 02, defer until multi-instance deployment or presence load forces it.
- [ ] **T-14** — *Later.* Non-functional targets: expected concurrent users, acceptable latency, uptime goal. Stub numbers are fine; the value is in having something to test against.

## F — Frontend

- [ ] **F-1** — *Blocking.* Build tooling and router. Vite + React Router is the conventional default.
- [ ] **F-2** — *Blocking.* State management split: what handles server state, what handles client state. Doc 04 explains why these should be different tools.
- [ ] **F-3** — *Blocking.* **Contrast audit.** Measure the low-opacity text against the dark backdrop. Several combinations at `.45`–`.62` opacity are likely below WCAG AA. Establish a minimum opacity for text-bearing elements and apply it consistently.
- [ ] **F-4** — *Blocking.* `prefers-reduced-motion` handling for all four infinite animations. Non-negotiable for a screen users stare at for 25 minutes.
- [ ] **F-5** — *Blocking.* Responsive strategy per **P-4**. The three-column timer layout needs a defined collapse behaviour.
- [ ] **F-6** — *Soon.* Styling approach: CSS Modules, Tailwind, or CSS-in-JS. The prototype is inline styles, which is a prototype artifact, not a recommendation — but keep the token layer as CSS custom properties regardless, so theming (**D-11**, cosmetics) stays cheap.
- [ ] **F-7** — *Soon.* Component library: build primitives or adopt a headless library. The visual identity is distinctive enough that heavy component libraries will fight you.
- [ ] **F-8** — *Soon.* Design the eight missing screens listed in doc 04.
- [ ] **F-9** — *Soon.* **Empty states.** Specifically the day-one profile. This is the first impression of a motivational product and it currently shows zeros.
- [ ] **F-10** — *Soon.* Keyboard accessibility and focus indicators. The prototype has hover styles but no visible focus rings.
- [ ] **F-11** — *Soon.* Tab-title and favicon behaviour during a running cycle — showing remaining time in the tab title is a small feature with outsized value for a background timer.
- [ ] **F-12** — *Later.* Internationalisation. If the team is Brazilian, decide now whether Portuguese ships at launch; retrofitting i18n is significantly more expensive than starting with it.
- [ ] **F-13** — *Later.* Avatar uploads — storage, resizing, moderation. The prototype uses generated initials, which is a perfectly good permanent answer.

## Q — Quality & process

- [ ] **Q-1** — *Blocking.* Definition of done. Include: tests written, migration reviewed, docs updated.
- [ ] **Q-2** — *Blocking.* Branching model and code review rules — reviewers required, and whether the author may merge.
- [ ] **Q-3** — *Blocking.* Issue tracker and how a ticket moves across the board.
- [ ] **Q-4** — *Soon.* Testing strategy. At minimum: unit tests for progression rules and `deriveStage`, integration tests with Testcontainers against real Postgres, and a handful of end-to-end tests on the core loop. Coverage targets are less useful than naming which layers are non-negotiable.
- [ ] **Q-5** — *Soon.* Ownership. Who owns backend, frontend, schema, and design decisions? Small teams often skip this and then discover the schema has three authors.
- [ ] **Q-6** — *Soon.* Documentation upkeep — these docs go stale within a month unless updating them is part of **Q-1**.
- [ ] **Q-7** — *Later.* Performance budget and load testing.

---

## Suggested sequencing

The dependency order that unblocks the most work fastest:

**1. Product foundation** — P-1, P-3, P-4, P-7
Nothing can be estimated until the MVP line and target platform are fixed.

**2. Domain rules** — D-1, D-2, D-5, D-6, D-7, D-8
These are cheap to decide and expensive to retrofit. They are also the direct inputs to the ER session, so hold them before it.

**3. ER session** — using doc 03 as the starting point, informed by step 2.

**4. Technical foundation** — T-1 through T-5, A-1, A-2, F-1, F-2, Q-1 through Q-3
Everything needed to write the first real commit.

**5. Accessibility and design gaps** — F-3, F-4, F-5, F-9
Deliberately early. All four get exponentially more expensive after the component library exists.

**6. Legal** — L-1, L-2, L-3
L-1 in particular should be resolved before anyone estimates the music feature, because the answer may remove the feature.

**7. Co-op rooms** — R-1 through R-8, only when the feature is actually scheduled.

## Highest-risk items

If you only chase five things this week:

| Item | Why it's dangerous |
|---|---|
| **P-1** MVP boundary | Without it, the team builds breadth instead of depth and nothing ships |
| **D-7** Streak/timezone | Timezone-naive streak logic is nearly guaranteed to produce user-visible bugs, and it corrupts the data it touches |
| **L-1** Music licensing | Legal blocker that can eliminate a planned feature after it's been designed and estimated |
| **R-1** Room model | "Shared timer" and "parallel timers with presence" differ by an order of magnitude in cost, and both are currently called the same thing |
| **F-3/F-4** Accessibility | The aesthetic (low opacity, constant motion) is in direct tension with accessibility, and the fix is far cheaper before the design system is codified than after |
