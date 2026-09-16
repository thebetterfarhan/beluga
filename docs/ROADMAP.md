# Roadmap

Status labels: **Now**, **Next**, **Planned**, **Later**, and **Complete**. Move work only when its prerequisites and validation evidence are clear.

## Phase 1 — Core stability — Planned

**Goal:** Keep launcher startup, package discovery, favorites, channels, and app launching reliable.

**Success criteria**

- Core launcher UI remains stable through package and channel refreshes.
- App launch and return do not lose the user in an unusable focus state.
- Regressions have focused automated checks where practical.

## Phase 2 — Accessibility foundation — Now

**Goal:** Establish a dependable D-pad and TalkBack baseline throughout the existing launcher.

**Current progress**

- App-card semantics are merged so the TV card stays focusable while its decorative artwork is silent.
- Toolbar, tabs, rows, cards, popups, and settings have targeted focus and semantics work.
- Chromecast build/install and hierarchy checks have completed; manual TalkBack speech validation remains.

**Exit criteria**

- Manual device checklist passes for core navigation, cards, popups, settings, and app return.
- No known duplicate labels, focus traps, or unexpected focus theft remain in the core paths.

## Phase 3 — Navigation polish — Next

**Goal:** Make navigation history and restoration understandable and consistent.

**Success criteria**

- Back behavior is confirmed with blind-user testing for tabs, popups, and settings.
- Focus-restoration choices have documented, tested behavior and safe fallbacks.
- Complete launcher-state restoration is either implemented or clearly deferred.

## Phase 4 — Accessibility settings — Now

**Goal:** Give users accessible, persistent control of orientation-related behavior.

**Success criteria**

- Accessibility overview and focus-restoration pages are usable with D-pad and TalkBack.
- Selected values persist and the default is documented.
- All offered modes either work as described or are labelled as unavailable/coming soon.

## Phase 5 — Performance and resilience — Now

**Goal:** Keep interaction responsive on supported TV hardware and resilient to live data changes.

**Current progress**

- Stale-refresh guard implemented (`LauncherActivity`, `REFRESH_STALE_MS = 60_000L`); live app changes still covered by `PackageChangeReceiver`.
- Preview-channel program commits batched into a single transaction (`ChannelRepository`).
- Chromecast measurement captured: cold-start apps refresh 3,354 ms; return after >60 s 1,470 ms; return within 60 s correctly skips the refresh.
- Back-transition main-thread jank (~1–2 s `Davey!` frames) scoped as a separate task.

**Success criteria**

- Navigation responsiveness is measured on hardware after significant interaction changes.
- Refreshes do not steal focus or cause repeated speech.
- Main-thread work in focus paths is minimized.

## Phase 6 — Blind productivity features — Planned

**Goal:** Add high-value navigation aids based on validated user needs.

Possible work: accessible search, recently used apps, hidden-app management, startup summary, and accessibility profiles.

## Phase 7 — Customization — Later

**Goal:** Offer user-controlled layout and behavior without compromising predictable navigation.

Possible work: configurable toolbar, layouts, navigation sounds, themes, and plugin support.

## Phase 8 — Release candidate — Later

**Goal:** Stabilize a documented, device-validated candidate.

**Success criteria:** clean staging build, full manual TalkBack pass, regression review, packaging audit, and explicit release approval.

## Phase 9 — Version 1.0 — Later

**Goal:** Publish a dependable launcher with an accessible core experience and sustainable maintenance practices.
