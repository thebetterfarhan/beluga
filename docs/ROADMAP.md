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

## Phase 6 — Blind productivity features — Complete

**Goal:** Add high-value navigation aids based on validated user needs.

**Success criteria**

- Recently used apps are surfaced in a way that is accessible and predictable.
- Accessible search is usable with D-pad and TalkBack.

**Completed:**
- Continue row and Recent apps row implemented and validated on device.
- Accessible search bar with D-pad navigation and TalkBack filtering.
- Hidden-app management: hide from All Apps grid, unhide from Settings → Accessibility → Hidden apps, persistence across restarts.
- Startup TalkBack announcement: Home tab announces section structure on resume; All Apps announces app count on resume.
- Accessibility audit: 14 issues fixed across 10 files (Role.Button on cards, heading() on screen titles, focusRestorer() on popups and screens, contentDescription on clock, CardRow subtitle semantics).

## Phase 7 — Customization — Later

**Goal:** Offer user-controlled layout and behavior without compromising predictable navigation.

Possible work: configurable toolbar, layouts, navigation sounds, themes, and plugin support.

## Phase 8 — Release candidate — Later

**Goal:** Stabilize a documented, device-validated candidate.

**Success criteria:** clean staging build, full manual TalkBack pass, regression review, packaging audit, and explicit release approval.

## Phase 9 — Version 1.0 — Later

**Goal:** Publish a dependable launcher with an accessible core experience and sustainable maintenance practices.

## Phase 10 — Home button remap — Complete

**Goal:** Make BlindPilot respond to the Home button on Google TV.

**Success criteria**

- User can set BlindPilot as the default launcher from within the app.
- TalkBack-aware prompt encourages default launcher setup when an accessibility service is active.

**Completed:**

- Default Launcher card in Settings → Accessibility using `DefaultLauncherHelper.requestDefaultLauncherIntent()`.
- `AccessibilityServicesHelper` detects TalkBack; targeted prompt shown when TalkBack is active.
- Broken `HomeRemapAccessibilityService` removed (Android blocks HOME key interception via accessibility services on Google TV).

## Phase 11 — Channel editing — Complete

**Goal:** Allow users to reorder the channel rows on the Home screen.

**Success criteria**

- Channels appear in user-defined order on the Home screen.
- Order persists across launcher restarts.
- Reordering is accessible via D-pad and TalkBack.

**Completed:**

- `ChannelPreferences` (SharedPreferences-backed store), `ChannelPreferencesViewModel` with `moveLeft`/`moveRight`.
- `ChannelPreferencesScreen` with LazyColumn and move buttons (TalkBack `Role.Button` semantics).
- Channel order applied in `HomeTabViewModel` via `orderChannels()` helper.
- Database migration `2.sqm` adds `Channel.weight` column on schema upgrade.
- Accessible from Settings → Accessibility → Channel order.

## Phase 12 — Performance optimization — Complete

**Goal:** Reduce redundant computation in hot paths.

**Success criteria**

- No repeated string parsing on every SharedPreferences read.
- No double-read on store mutations.

**Completed:**

- `HiddenAppsStore` memoizes parsed `Set<String>` — avoids `split()` on every `get()`/`isHidden()`.
- `hide/unhide/unhideAll` return updated set — eliminates double-read in ViewModels.
- `ChannelProgramCard.accessibleLabel()` memoized with `remember(program.id)`.
