# Roadmap

Status labels: **Now**, **Next**, **Planned**, **Later**, and **Complete**. Move work only when its prerequisites and validation evidence are clear.

## Phase 1 — Core stability — Planned

**Goal:** Keep launcher startup, package discovery, favorites, channels, and app launching reliable.

**Success criteria**

- Core launcher UI remains stable through package and channel refreshes.
- App launch and return do not lose the user in an unusable focus state.
- Regressions have focused automated checks where practical.

## Phase 2 — Accessibility foundation — Complete

**Goal:** Establish a dependable D-pad and TalkBack baseline throughout the existing launcher.

**Exit criteria**

- Manual device checklist passes for core navigation, cards, popups, settings, and app return.
- No known duplicate labels, focus traps, or unexpected focus theft remain in the core paths.

## Phase 3 — Navigation polish — Next

**Goal:** Make navigation history and restoration understandable and consistent.

**Success criteria**

- Back behavior is confirmed with blind-user testing for tabs, popups, and settings.
- Focus-restoration choices have documented, tested behavior and safe fallbacks.
- Complete launcher-state restoration is either implemented or clearly deferred.

## Phase 4 — Accessibility settings — Complete

**Goal:** Give users accessible, persistent control of orientation-related behavior.

**Success criteria**

- Accessibility overview and focus-restoration pages are usable with D-pad and TalkBack.
- Selected values persist and the default is documented.
- All offered modes either work as described or are labelled as unavailable/coming soon.

## Phase 5 — Performance and resilience — Complete

**Goal:** Keep interaction responsive on supported TV hardware and resilient to live data changes.

**Success criteria**

- Navigation responsiveness is measured on hardware after significant interaction changes.
- Refreshes do not steal focus or cause repeated speech.
- Main-thread work in focus paths is minimized.

**Key work**

- Stale-refresh guard (`REFRESH_STALE_MS = 60_000L`) + `PackageChangeReceiver` for live changes.
- Preview-channel program commits batched into one transaction.
- Back-transition jank removed (placement-counter feedback loop eliminated).
- Baseline-profile architecture added (`:baselineprofile` module, committed `baseline-prof.txt`).
- Physical-remote + TalkBack pass completed (2026-09-16).

## Phase 6 — Blind productivity features — Now

**Goal:** Add high-value navigation aids based on validated user needs.

**Current progress**

- Continue row and Recent apps row implemented and validated on device.

**Success criteria**

- Recently used apps are surfaced in a way that is accessible and predictable.
- Accessible search is usable with D-pad and TalkBack.

Possible work: accessible search, recently used apps, hidden-app management, startup summary, and accessibility profiles.

## Phase 7 — Customization — Later

**Goal:** Offer user-controlled layout and behavior without compromising predictable navigation.

Possible work: configurable toolbar, layouts, navigation sounds, themes, and plugin support.

## Phase 8 — Release candidate — Later

**Goal:** Stabilize a documented, device-validated candidate.

**Success criteria:** clean staging build, full manual TalkBack pass, regression review, packaging audit, and explicit release approval.

## Phase 9 — Version 1.0 — Later

**Goal:** Publish a dependable launcher with an accessible core experience and sustainable maintenance practices.
