# Changelog

All notable changes to Beluga are documented here.

## [Unreleased] — Beluga

### Features

- **Focus Restoration** — configurable startup focus: last focused app, first favorite, Home tab, All Apps tab, or full state restoration
- **Onboarding Card** — first-launch TalkBack guide to D-pad controls, shown once and dismissable
- **Accessibility Overview** — centralized hub with system accessibility status, audio descriptions, high-contrast text, and a recently updated apps card
- **Recent Updates Tracker** — `PendingUpdatesStore` records `PACKAGE_REPLACED` broadcasts with a 24-hour window; count announced on All Apps tab entry
- **Package Change Receiver** — refreshes the app list on `PACKAGE_ADDED`, `PACKAGE_CHANGED`, `PACKAGE_REPLACED`, `PACKAGE_REMOVED`, and `BOOT_COMPLETED`
- **Continue Row** — jumps straight back into the last focused app
- **Recent Apps Row** — shows up to 4 most-recently opened apps, most-recent first
- **Accessible Search** — `AppsSearchBar` filters `displayName` and `packageName` case-insensitively; empty state announced; D-pad DOWN from search routes to first result
- **Startup Announcement** — dynamic TalkBack announcement on launcher resume: Home announces visible section names, All Apps announces app count
- **Hidden Apps** — hide apps from the grid without uninstalling; accessible management screen in Settings → Accessibility
- **Channel Reordering** — drag-and-drop or ◀/▶ buttons to reorder TV channel rows; persistence via SharedPreferences
- **Default Launcher Card** — honest state detection for the stock Google TV launcher; opens system App-Info page or Default-home chooser
- **App Renamed to Balooga** — display name changed from "TV Launcher" to "Balooga"

### Bug Fixes

- **Cursor leak (ChannelResolver)** — all three cursor-iterating methods now use `cursor.use {}` to guarantee cleanup on any exception
- **Silent exception swallowing (PackageChangeReceiver)** — empty `catch` replaced with `Timber.e()` logging
- **Race: HiddenAppsViewModel** — `_hiddenApps` is now a pure `combine().stateIn()` derived flow; the DB collector can no longer overwrite a user-initiated unhide
- **Race: ChannelPreferencesViewModel** — channel list is now a pure derived flow; manual reorders update SharedPreferences without mutating the displayed state
- **Unsafe `as Drawable` cast (TvInputExtensions)** — now returns nullable `Drawable?`; no more `TypeCastException` on unknown input types
- **Focus trap: Home tab vertical scroll** — `focusRestorer()` added to Home `LazyColumn`; focus returns to prior viable target on subtree re-entry
- **Popup mutation timing** — popup dismisses before changing favorite or order data; stale popup no longer visible during data mutation
- **Duplicate heading announcement (Favorites row)** — `titleRes = null` on `AppCardRow` so `HeadingText` is the sole heading; "Favorite apps" announced once
- **Back-from-Apps lands on Settings gear** — toolbar-level `focusRestorer()` removed; Back now restores to All Apps tab, not the Settings icon
- **Focus-restoration dialog text duplication** — merged card semantics use `stateDescription` for value text; inner `Text` nodes use `clearAndSetSemantics` to prevent re-announcement; redundant `heading()` removed from dialog title
- **OrientationHelpScreen heading-vs-paneTitle duplication** — redundant `heading()` removed; section subtitles retain `heading()` for heading navigation
- **NPE in App.createDrawable** — both stored intent URIs could be null; guard now falls back to `PackageManager.defaultActivityIcon`
- **Home tab shows empty Favorites heading** — row now conditional-skips when `apps.isEmpty()`; Watch Next row similarly hidden when empty
- **Navigation backstack survives config changes** — `DestinationListSaver` persists backstack across configuration changes via `rememberSaveable`
- **URISyntaxException crash on card click** — `Intent.parseUri` wrapped in try/catch; malformed stored URIs no longer crash the launcher
- **Long-press timing fix** — debug input marker ignores repeated key-down events so one physical press is measured once

### Performance

- **SharingStarted.WhileSubscribed(5000)** — replaced `Eagerly` in `AppsTabViewModel`; upstream collection stops when the tab is not visible
- **N+1 channel subscriptions eliminated** — single `StateFlow<Map<channelId, programs>>` in `HomeTabViewModel` replaces one `collectAsState()` per visible channel row
- **SharedPreferences caching** — six stores (`RecentAppsStore`, `LastFocusedAppStore`, `ChannelPreferences`, `AccessibilityPreferences`, `LauncherStateStore`, `PendingUpdatesStore`) now cache reads in `@Volatile` fields
- **LazyColumn keys** — all 11 `item {}` blocks in `AccessibilityOverviewScreen` now have explicit keys; no full-list recomposition on onboarding dismiss
- **Debug logging** — `Timber.DebugTree()` and `DebugLogger()` now gated behind `BuildConfig.DEBUG`
- **Coil crossfade** — `AsyncImage` crossfade enabled globally
- **HiddenAppsStore memoization** — cached parsed `Set<String>` avoids repeated `split()` on every `get()`/`isHidden()` call
- **ChannelProgramCard accessibleLabel memoization** — `remember(program.id)` prevents recomposition on unrelated state changes
- **Baseline profile** — cold-start profile collected and embedded in release APK; `:baselineprofile` module with `BaselineProfileGenerator`

### Accessibility

- **`Role.Button` on cards** — `AppCard` and `ChannelProgramCard` now declare `role = Role.Button` in semantics
- **Focus restoration composable** — `focusRestorer()` applied to: `OrientationHelpScreen`, `FocusRestorationScreen`, both `AppPopup` rows, `PopupContainer` Box, `AppsTab` LazyVerticalGrid, and Home `LazyColumn`
- **ToolbarClock contentDescription** — `Text` now carries `"Current time: $time"`
- **CardRow subtitle merged into heading** — subtitle text merged into `contentDescription` so TalkBack announces heading + subtitle as one unit
- **ChannelProgramCardDetails text silenced** — `clearAndSetSemantics` prevents duplicate TalkBack reading of title and description that are already in the card's `contentDescription`
- **AppsTab heading restructured** — `heading()` moved to standalone `Column` child outside the grid; `paneTitle = "All apps"` on the column
- **Accessible search bar** — `BasicTextField` with `contentDescription = "Search apps"`; clear button; D-pad DOWN routes to first result via `focusRestorer()`
