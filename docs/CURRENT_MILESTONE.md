# Current Milestone: Blind productivity features

**Status:** In progress

## Objective

Add high-value navigation aids based on validated user needs. The Continue row and Recent apps row are the first features in this phase, providing blind users with quick access to recently used apps without needing to search through the full grid.

## Scope

- Continue row: show the last-focused app with an intent to resume.
- Recent apps row: show up to 4 most-recently opened apps.
- MRU tracking via `RecentAppsStore` with a cap of 8 entries.
- Accessible presentation: each row is a single heading with a subtitle, cards are single-focus targets.
- Accessible search: text filter for the All Apps grid, usable with D-pad and TalkBack.

## Out of scope

- Changes to Home routing, launcher registration, or package identity.

## Completed and evidenced

- [x] `RecentAppsStore`: bounded MRU list (cap 8) via SharedPreferences, with `add()`, `get()`, `clear()`, and round-trip persistence.
- [x] `HomeTab` reorganized: Continue → Recent → Favorites → Watch Next/Channels rows.
- [x] Continue row: shows `lastFocusedAppId` from `LastFocusedAppStore` StateFlow; falls back to most-recent app if last-focused app is uninstalled.
- [x] Recent row: shows up to `RecentRowCap = 4` most-recent app IDs from `RecentAppsStore`, most-recent-first.
- [x] `AppCardRow.onFocused` calls `viewModel.recordOpenedApp()` to update the MRU list; `recentAppIdsFlow` StateFlow drives recomposition.
- [x] `CardRow` subtitle support: optional body-small text rendered below the heading.
- [x] `HeadingText` composable: renders `heading()` semantics text with `titleMedium` typography, used for all row headings.
- [x] Favorites row: `titleRes = null` on `AppCardRow` so `HeadingText` is the sole heading (fixes TalkBack duplicate announcement bug in `abf9133`).
- [x] All 8 new test classes pass: `RecentAppsStoreTest` (7), `FocusRestorationManagerTest` (4), `LastFocusedAppStoreTest` (4), `LauncherStateStoreTest` (5), `LauncherStateRecorderTest` (5), `AccessibilityPreferencesTest` (4), `DestinationMappingTest` (4), `RefreshStalenessTrackerTest` (3).
- [x] `assembleDebug` / `lintDebug` / `testDebugUnitTest` green.
- [x] Physical-remote + TalkBack pass completed (2026-09-16): all checklist items pass including Continue row, Recent row, Favorites heading (fixed), D-pad navigation, popup round-trip.
- [x] All Apps accessible search: `AppsSearchBar` with `BasicTextField`, search icon, clear button. `contentDescription = "Search apps"`. Filters `displayName` and `packageName` case-insensitively. Empty search state shows "No apps found". D-pad DOWN from search bar routes to first app card via `focusRestorer(firstCardFocusRequester)`.
- [x] All Apps heading restructured outside grid: `heading()` on a standalone `Column` child instead of a `GridItemSpan` grid item. `Column` has `paneTitle = "All apps"` semantics.
- [x] Physical TalkBack validation of search bar completed (2026-09-16).
- [x] Startup announcement: `Lifecycle.Event.ON_RESUME` triggers TalkBack announcement on launcher resume. Home tab announces "Home tab. Continue, Recent, Favorites, Watch Next." (visible sections only). All Apps announces "All apps. Search installed apps. N apps." Uses `DisposableEffect` + `LaunchedEffect` with a `resumeCount` counter to trigger only on genuine resume events, not recomposition.
- [x] Accessibility audit fixes (2026-09-16): `AppCard` and `ChannelProgramCard` now have `role = Role.Button` in semantics (TalkBack announces "Button" for app tiles and channel cards). `FocusRestorationScreen` and `OrientationHelpScreen` titles now have `heading()` semantics. `OrientationHelpScreen` now has `focusRestorer()`. AppPopup rows (home + apps) now have `focusRestorer()` for focus cycling within popup. `PopupContainer` Box and `AppsTab` LazyVerticalGrid now have `focusRestorer()`. `ToolbarClock` Text now has `contentDescription = "Current time: $time"`. `CardRow` heading now merges subtitle into `contentDescription` for combined TalkBack announcement.

## Current blockers and risks

- Install the TV build only on the Chromecast currently connected via wireless ADB (`sabrina`, Android 14); do not deploy it to any other device.
- `COMPLETE_LAUNCHER_STATE` is not implemented as a full state restore. Its visible wording must remain honest until it is implemented or removed.

## Exit criteria

- [x] Continue row shows last-focused app and launches it.
- [x] Recent row shows up to 4 most-recent apps with correct order.
- [x] Focusing a card updates the MRU list.
- [x] Row headings announced once by TalkBack.
- [x] D-pad navigation through all rows without traps.
- [x] Physical-remote + TalkBack pass completed with all items passing.
- [x] Search bar: TalkBack announces "Search apps" on focus; typing filters the grid; clear button works; D-pad DOWN moves to first result; empty state announced.
- [x] Hidden apps: long-press app in All Apps → Hide button; hidden apps filtered from grid and search; Settings → Accessibility → Hidden apps shows hidden list; tap to unhide; persistence across launcher restarts.
- [x] Startup announcement: TalkBack announces current tab structure on launcher resume (Home: section names; All Apps: app count).

## Next concrete action

Advance Phase 5 → Phase 6 in ROADMAP and begin planning accessible search or other Phase 6 backlog items from `docs/BACKLOG.md`.

## Future update template

```markdown
## Update — YYYY-MM-DD

- Completed:
- Evidence/checks:
- Open risk or blocker:
- Next concrete action:
```

## Update - 2026-09-15 (Google TV launcher management card)

- Completed: "Google TV launcher" settings card (state detection + system App-Info handoff, `GoogleTvLauncherHelper`); decision recorded that the launcher cannot toggle another package directly (device-owner constraints) and delegates to the system page.
- Evidence/checks: on-device hierarchy shows one merged name/state/action ("Google TV launcher. Disabled. ..."); activation opened `com.android.tv.settings/.device.apps.AppManagementActivity`; Back returned focus to the same card; `assembleDebug`/lint/unit tests green.
- Open risk or blocker: spoken wording requires manual TalkBack pass; baseline-profile generation task still exits nonzero after successful runs (workaround documented).
- Next concrete action: manual TalkBack + physical-remote checklist (Apps-grid flicker, last-favorite row-end jump, popup wording, app-card options, app return, Watch Next), including the new Google TV launcher card wording.

## Update - 2026-09-16 (Phase 5 complete — manual TalkBack pass)

- Completed: Committed `abf9133` — Favorites heading duplicate fix. Physical-remote + TalkBack pass completed on `abf9133` with all checklist items passing.
- Continue row: correct app name + subtitle announced once.
- Recent row: up to 4 most-recent apps with correct subtitle.
- Favorites heading: "Favorite apps" announced once (previously announced twice due to `titleRes` defaulting to `R.string.favorite_apps` while `HeadingText` also rendered it with `heading()`).
- D-pad navigation: Continue → Recent → Favorites → Watch Next/Channels flows in order; no focus traps.
- Card popup: focus enters popup, Back returns to invoking card.
- Phase 5 marked Complete in ROADMAP; Phase 6 "Blind productivity features" marked Now.
- Next concrete action: begin Phase 6 backlog — accessible search or other items from `docs/BACKLOG.md`.

## Update - 2026-09-16 (Hidden apps feature — Phase 6 complete)

- Completed: Committed `3154116` — Hidden apps management feature. `HiddenAppsStore` (SharedPreferences `Set<String>`), hide button on app popup (`Icons.Default.Close`), hidden apps filtered from grid and search, `HiddenAppsScreen` with accessible unhide list in Settings → Accessibility, `HiddenAppsViewModel` with `unhide` and `unhideAll`, full navigation wiring + state persistence.
- Evidence/checks: `assembleDebug` / unit tests green, installed on device, physical TalkBack pass: hide button announced, app disappears from grid and search, Hidden Apps screen accessible, unhide restores app, persistence across restarts.
- Next concrete action: Phase 6 remaining backlog item — startup summary.

## Update - 2026-09-16 (Startup announcement + accessibility audit)

- Completed: TalkBack announcement on launcher resume via `DisposableEffect` + `LaunchedEffect` + `resumeCount` counter. Home tab: dynamic section list. All Apps: app count. Accessibility audit fixing 14 issues across 10 files: `Role.Button` on `AppCard` and `ChannelProgramCard`; `heading()` on `FocusRestorationScreen` and `OrientationHelpScreen` titles; `focusRestorer()` on `OrientationHelpScreen`, both `AppPopup` rows, `PopupContainer` Box, and `AppsTab` LazyVerticalGrid; `ToolbarClock` contentDescription; `CardRow` subtitle merged into heading `contentDescription`.
- Evidence/checks: `assembleDebug` / unit tests green, installed on device.
- Next concrete action: Phase 6 is complete — all backlog items done. Review ROADMAP for next milestone.
