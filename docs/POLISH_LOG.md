# Accessibility Polish Log

## 2026-09-16 - Manual TalkBack + physical-remote pass

- **Completed:** Full physical-remote + TalkBack pass on `abf9133` (Favorites heading fix).
- **Continue row:** Appears when last-focused app exists; correct app name + subtitle announced.
- **Recent row:** Shows up to 4 most-recent apps; "Apps opened recently." subtitle announced.
- **Favorites heading:** Fixed duplicate heading announcement — `titleRes = null` on AppCardRow so HeadingText() is the sole heading. TalkBack now announces "Favorite apps" once.
- **D-pad navigation:** Continue → Recent → Favorites → Watch Next/Channels flows in order; no focus traps.
- **Card popup:** Focus enters popup, Back returns to invoking card.
- **All core checklist items:** Passed.
- **Bug found:** Favorites heading announced twice — fixed in `abf9133`.
- **Result:** All checks Pass.

## 2026-09-16 - All Apps accessible search + heading restructure

- **Completed:** Committed `ab40130` — All Apps accessible search bar + heading restructure. 4 files, +247/−53 lines.
- **Search bar:** `AppsSearchBar` with `BasicTextField`, search icon, clear button. `contentDescription = "Search apps"` so TalkBack announces the field name on focus. D-pad DOWN from search bar routes to first app card via `focusRestorer(firstCardFocusRequester)`.
- **Filtering:** `AppsTabViewModel` exposes `searchQuery` StateFlow + `filteredApps` derived flow. Case-insensitive filter on `displayName` and `packageName`. Empty search shows "No apps found" state.
- **Heading restructure:** "All apps" heading moved OUTSIDE `LazyVerticalGrid`. Previously a full-width `GridItemSpan` grid item (participated in grid focus order); now a standalone `Column` child with `heading()` semantics. `Column` has `paneTitle = "All apps"` for TalkBack pane entry announcement.
- **New strings:** `search_apps`, `search_apps_placeholder`, `search_clear`, `search_result_count`, `search_result_count_singular`, `search_no_results`.
- **Verification:** `assembleDebug`/`lintDebug`/`testDebugUnitTest` green. Physical TalkBack validation completed — search bar, DOWN navigation, clear button, empty state all pass.
- **Note:** Previously committed Favorites heading fix (`abf9133`) also on this date.

## 2026-09-16 - Continue + Recent apps feature committed

- **Completed:** Committed `cf8a328` — Home tab Continue row + Recent apps row feature. 31 files, +1,062/−56 lines.
- **Continue row:** Single card showing the last-focused app with subtitle "Open the last app you were using." Powered by `LastFocusedAppStore` StateFlow + `RecentAppsStore` MRU list.
- **Recent row:** Up to 4 most-recently opened apps (most-recent-first). MRU cap of 8 in `RecentAppsStore`; displayed as 4 in the UI (`RecentRowCap = 4`).
- **Tracking:** `AppCardRow.onFocused` calls `viewModel.recordOpenedApp(app.id)`, updating `RecentAppsStore` and triggering recomposition via `recentAppIdsFlow` StateFlow.
- **New util/ classes:** `PreferenceStore`, `SharedPreferenceStore`, `RecentAppsStore`, `FocusRestorationManager`, `LastFocusedAppStore`, `LauncherStateStore`, `LauncherStateRecorder`, `LauncherStateSnapshot`, `AccessibilityPreferences`, `FocusRestoreMode`, `DestinationMapping`, `GoogleTvLauncherHelper`, `RefreshStalenessTracker`, `DebugTrace`, `startupFocus`, `debugFocusLog`.
- **Tests:** 8 new test classes covering all new util classes. `RecentAppsStoreTest` (7 cases), `FocusRestorationManagerTest` (4), `LastFocusedAppStoreTest` (4), `LauncherStateStoreTest` (5), `LauncherStateRecorderTest` (5), `AccessibilityPreferencesTest` (4), `DestinationMappingTest` (4), `RefreshStalenessTrackerTest` (3). Plus `InMemoryPreferenceStore` fixture.
- **Verification:** `assembleDebug`, `lintDebug`, `testDebugUnitTest` all green. No new lint warnings introduced.
- **Previous state:** These changes existed as uncommitted WIP from the previous session. Verified they build and test cleanly before committing.

## 2026-09-14 - Focus-restoration detail semantics check

- **Validation:** Opened Focus restoration without changing its selected mode. The selected `Home tab` row was the only focused launcher node.
- **Result:** Its hierarchy exposes `Home tab. Always starts on the Home tab.`, checked state, and a RadioButton role. `Settings` is the page context exposed elsewhere in the hierarchy, not an unlabeled focused target.
- **Implementation finding:** The entry-placement debug counters are remembered ordinary objects, not Compose state. They do not cause recomposition feedback; no production change was warranted.
- **Limitation:** A subsequent Back check was obscured by the Chromecast Dream overlay, so it is not evidence for return focus. Physical TalkBack wording and remote validation remain required.

## 2026-09-14 - Card-menu focus restoration hierarchy check

- **Validation:** Focused `Just Player`, issued a long D-pad-center press, then dismissed the resulting menu with Back.
- **Result:** The popup had exactly one focused launcher node labeled `Remove from favorites`; Back restored exactly one focused node labeled `Just Player`.
- **Limitation:** This does not verify spoken pane/action wording, duplicate speech, disabled reorder actions, or favorite-mutation behavior. Those remain physical TalkBack checks.

## 2026-09-14 - All Apps Back hierarchy observation

- **Validation:** Activated All apps, moved Down to the first grid card (`Cx File Explorer`), then pressed Back once. The launcher stayed top-resumed and every snapshot had exactly one focused launcher node.
- **Observed focus:** Back returned focus to Settings rather than the All apps tab or Home.
- **Decision required:** This is not changed from hierarchy evidence alone. Blind-user/TalkBack testing must determine whether Settings is the intended recovery target before changing Back or focus-restoration behavior.

## 2026-09-14 - Visible launcher D-pad hierarchy check

- **Validation:** With the Chromecast awake and the launcher visibly foregrounded, uiautomator exposed 100 launcher nodes and exactly one focused launcher node. D-pad traversal was Home -> All apps -> Settings -> All apps -> Home -> Just Player -> Home.
- **Result:** Each focused target had the expected label, and no focus loss or duplicate focused node appeared in these snapshots. The check did not activate navigation or alter favorites.
- **Limitation:** Hierarchy inspection cannot verify TalkBack wording, duplicate speech, speech timing, popup behavior, or physical-remote handling. The manual checklist remains required.

## 2026-09-14 - Executable refresh-staleness regression coverage

- **Change:** Added the platform-independent `RefreshStalenessTracker` and a JUnit regression test. `LauncherActivity` uses one tracker for apps and one for channels.
- **Why it matters:** The stale-until-success rule now has automated coverage, including the exact 60-second boundary. A failed or cancelled repository call cannot be mistaken for a successful refresh by this state holder.
- **Evidence:** `:app:testDebugUnitTest --tests nl.ndat.tvlauncher.util.RefreshStalenessTrackerTest`, `:app:assembleDebug`, and `:app:lintDebug` passed. The debug APK installed successfully to `sabrina`; `LauncherActivity` was top-resumed, TalkBack was enabled, and the debug log recorded `focus: settings` without an application crash.
- **Accessibility limitation:** The accessibility hierarchy still exposed the Chromecast Dream overlay, whose focused control was Screensaver settings. It did not expose launcher nodes, so it cannot validate launcher startup focus, semantics, D-pad traversal, or spoken output.
- **Next action:** Run the remaining physical TalkBack/remote checklist with the launcher visibly foregrounded. Keep the Dream overlay from being used as launcher accessibility evidence.

## 2026-09-14 - Refresh retry correctness

- **Issue found:** `LauncherActivity` marked apps and channels fresh before their refresh calls completed. A failed or lifecycle-cancelled call could then suppress the retry for the 60-second staleness window.
- **Fix implemented:** Each timestamp now updates only after a successful refresh. `CancellationException` propagates from `repeatOnLifecycle` instead of being swallowed by the generic failure logger.
- **Validation performed:** `:app:clean`, `:app:assembleDebug`, `:app:lintDebug`, and `:app:testDebugUnitTest` completed successfully. The unit-test task has no sources. The debug APK installed successfully to the documented Chromecast (`sabrina`), `LauncherActivity` was top-resumed after explicit launch, TalkBack service was enabled, and filtered logs showed no launcher crash.
- **Accessibility limitation:** The visible UI hierarchy was the system launcher because a Dream/default-launcher overlay owned the window. It cannot validate this launcher's startup focus, semantics, D-pad order, or spoken output. Those physical TalkBack/remote checks remain open.
- **Performance observation:** This change only corrects failure handling; it adds no work to successful focus or refresh paths.
- **Next action:** Complete the remaining manual TalkBack and physical-remote checklist, then capture startup focus with the launcher visibly foregrounded.

This is a concise evidence log, not a substitute for the current milestone or test plan. Add an entry after meaningful behavior, accessibility, or performance work.

## 2026-09-12 — Accessibility foundation status

- Dedicated Accessibility and Focus restoration pages were added. Focus-restoration selection persists; `LAST_FOCUSED_APP` restores a stored favorite when viable and otherwise falls back safely.
- App-card semantics were refined so the Compose TV card retains D-pad focus while decorative artwork is silent. This addresses duplicate app-name speech without replacing the TV Material focus target.
- Home focus restoration, popup mutation timing, and debug-only focus instrumentation were refined. Measured Chromecast focus moves were 13–33 ms; first popup action focus was 229 ms.
- The debug APK was built, installed, and explicitly launched on Chromecast with Google TV running Android 14. `LauncherActivity` was top-resumed. TalkBack was enabled and the exposed hierarchy was spot-checked.
- Manual TalkBack wording, full physical-remote traversal, popup/list-mutation behavior, Settings return, app return, and Back-path preference remain to be validated. Home-button routing is deliberately out of scope for this milestone.

## 2026-09-12 — Automated-check note

- Assembly produced the updated debug APK and lint completed with zero errors and nine pre-existing warnings.
- A combined local test command did not return a final unit-test status. No test result is claimed from that run.

## 2026-09-13 — Accessibility-page orientation follow-up

- Change or finding: The split Accessibility overview and Focus restoration pages now declare accessibility pane titles in addition to their visible headings. The overview retains one merged, focusable summary row; the detail page retains a selectable radio group.
- Why it matters: A page title helps TalkBack users orient on entry without adding extra actionable nodes or changing TV-card focus behavior.
- Files or area affected: `AccessibilityOverviewScreen.kt` and `FocusRestorationScreen.kt`.
- Evidence: `:app:assembleDebug --offline --console=plain --no-daemon` completed successfully in 37 seconds. `:app:lintDebug --offline --console=plain --no-daemon` completed successfully in 2 minutes 19 seconds.
- Remaining validation or risk: The Chromecast was absent from wireless ADB discovery; only a Pixel was connected, so no installation, hierarchy check, or manual TalkBack test was attempted on the wrong device.
- Next action: Reconnect the Chromecast, deploy the latest APK to it only, then verify entry focus, pane orientation, Back restoration, radio wording, and physical-remote traversal.

## 2026-09-13 — Focus-restoration detail device finding

- Change or finding: Placement-based focus correctly moves initial focus to the selected radio option. However, Chromecast automation shows that subsequent Up, Down, Left, and Right presses leave focus on that option.
- Why it matters: A static focused radio option is a D-pad focus trap and prevents users from changing the preference independently.
- Files or area affected: `FocusRestorationScreen.kt`.
- Evidence: The latest APK installed successfully to the Chromecast; `LauncherActivity` was top-resumed and TalkBack remained enabled. UIAutomator showed the selected `Last focused app` radio as focused before and after Down input.
- Remaining validation or risk: Two targeted focus-graph implementations did not change device behavior. Spoken output and persistence validation remain untested.
- Next action: Use a stronger-model investigation to trace the Compose TV Card focus target and implement a device-verified traversal fix before resuming the manual TalkBack plan.

## 2026-09-13 — Focus-restoration radio-list trap fixed and device-validated

- Change or finding: Removed the custom `focusProperties { up/down }` focus graph and its FocusRequester chain from `FocusRestorationScreen.kt`. The list now uses the LazyContainer default focus search with `focusRestorer()`, and entry focus to the selected radio is requested from `onPlaced` via a snapshot guard so it applies exactly once.
- Why it matters: The custom graph was the only hand-wired focus search in the app and fought the internal `focusable()` that `androidx.tv` Material `Card` applies via `tvClickable` (verified in the tv-material 1.1.0 AAR). Default lazy search matches the proven Apps grid and Home rows.
- Files or area affected: `FocusRestorationScreen.kt`.
- Evidence: `:app:assembleDebug` successful (1m22s); `:app:lintDebug` successful (2m39s); `:app:testDebugUnitTest` NO-SOURCE (the project has no unit tests). On the connected Chromecast (Android 14, TalkBack enabled): Down moved LAST_FOCUSED_APP→FIRST_FAVORITE in 15ms and →HOME_TAB in 16ms; Up returned to FIRST_FAVORITE in 15ms. Hierarchy showed exactly one focused radio node with RadioButton role, correct checked state, and merged content description.
- Remaining validation or risk: Manual TalkBack spoken wording and physical-remote traversal still required.
- Next action: Run the manual TalkBack/physical-remote pass in `docs/TALKBACK_TEST_PLAN.md`.

## 2026-09-13 — Settings Back focus regression found and fixed on Chromecast

- Change or finding: After Back from the Focus restoration detail page, the Accessibility overview summary card earned focus but lost it ~307ms later to the always-present Settings toolbar button. Root cause: the overview used a one-shot `LaunchedEffect { delay(16); requestFocus() }` that could not recover when a late recomposition re-ran default focus search. The Back transition also stalls the main thread on this device (logcat `Davey!` frames ~1.3–2.1s, `Choreographer: Skipped 75 frames`), compounding the race. Fixed by matching the detail page's proven pattern: `focusRestorer()` on the LazyColumn and an `onPlaced` + snapshot-guarded `requestFocus()` that re-requests on every placement until accepted.
- Why it matters: A blind user Backing out of a settings page needs focus to return predictably to the page content, not to mutate the toolbar.
- Files or area affected: `AccessibilityOverviewScreen.kt`.
- Evidence: Pre-fix log showed `settings-overview focused=true ... request accepted=true` then `settings-overview focused=false` + `focus: settings` at +307ms (18:11:45.921). Post-fix (PID 11057) the same flow ends at `settings-overview focused=true hasFocus=true ... request accepted=true` with no subsequent steal. Post-fix traversal: card→Settings Up in 25ms, Down returns to the card. `:app:assembleDebug` and `:app:lintDebug` succeed.
- Remaining validation or risk: The BACK transition still shows ~1–2s main-thread jank on this device (package/app-asset work during recomposition); it no longer affects the focus outcome but is a measured performance item for a later scoped task. Manual TalkBack wording on the overview/detail Back flow is unverified.
- Next action: Confirm spoken wording on the overview/detail Back flow; measure the transition jank separately before optimizing it.

## 2026-09-13 — Home, popup, grid, and app-return focus evidence sweep on Chromecast

- Change or finding: End-to-end focus evidence on the connected Chromecast (Android 14, TalkBack enabled, launcher PID 11057, one activity record t23182 for the whole sweep):
  - Toolbar is bounded and trapp-free: RIGHT from Settings stays there (Clock is not focusable, no wrap); LEFT from Settings reaches the Home tab in 15 ms.
  - Back from the settings overview to Home restores focus to the Settings toolbar button (~28 ms), matching the checklist item.
  - All Apps: activation from the Home tab changes the tab (`tab changed` log), entry to the first grid card is 22 ms, grid moves are 15–16 ms. Hierarchy shows exactly one focused node per snapshot, one actionable node per app with one merged content description, and an "All apps" heading.
  - App-card popup: `--longpress KEYCODE_DPAD_CENTER` opens the card popup; the first action is focused (~635 ms including the long-press period). BACK dismissal issues an explicit `focus-request: app-card:app:com.esaba.downloader:popup-dismiss` and the hierarchy shows the invoking Downloader card focused.
  - Home favorites row moves are 12–16 ms. Launching SmartTube from its card and returning to the launcher (`am start` re-fronts the same task) produces zero focus-change logs and the hierarchy still shows the SmartTube card focused — the source card is preserved on app return.
  - 12 s idle on the All Apps grid produced zero `LauncherFocus` events (focus churn-free).
- Why it matters: These flows cover the milestone rules without a UI-hierarchy proof of the spoken layer: no traps, single focused node per snapshot, predictable restore after settings Back, popup dismissal, and app return.
- Files or area affected: none (validation only).
- Evidence: `LauncherFocus` logcat sequences as above; uiautomator hierarchies for the return and popup states; combined baseline `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` green in 14 s (code unchanged since the last full build).
- Remaining validation or risk: Two device observations need a judgement call in the manual TalkBack pass: (1) a one-off grid focus flicker (Downloader re-gained focus ~700 ms after a move at 18:26:13.415, settling immediately) did not reproduce over 12 s idle and is attributed to the RESUMED apps/channels refresh re-emitting mid-composition — re-check during a physical-remote session; (2) RIGHT from the last favorite jumps up to the toolbar Settings button (default focus search at a row end) — flagged as a pre-existing quirk; per the smallest-safe-change principle no hand-wired focus graph was added back.
- Next action: Manual TalkBack + physical-remote pass; separately scope the measured ~1–2 s Back-transition jank and the RESUMED refresh cost.

## 2026-09-13 — Phase 5: Performance and resilience (stale-refresh guard + channel batching)

- Change or finding: Replaced the every-`RESUMED` refresh in `LauncherActivity` with a stale-guarded refresh (`REFRESH_STALE_MS = 60_000L`). Live app changes are already handled by the registered `PackageChangeReceiver`, so re-querying on every return to the launcher was unnecessary and triggered a full apps/channels refresh (~3.3 s on Chromecast) plus recomposition and focus churn. Channels preview commits were batched into one transaction (`commitChannelPrograms(programsByChannel)`) so each watched channel-program query emits once per refresh instead of once per channel.
- Why it matters: Per the charter — "Refreshes do not steal focus or cause repeated speech" and "Main-thread work in focus paths is minimized". The old guard fired on every resume; the new one skips refreshes within the 60 s staleness window, removing the refresh storm on app return and reducing startup/return latency.
- Files or area affected: `LauncherActivity.kt` (stale-refresh guard + `debugLauncherLog` instrumentation), `ChannelRepository.kt` (batched preview-channel program commit).
- Evidence: Chromecast (`sabrina`, Android 14) device measurement, `LauncherFocus` instrumentation:
  - Cold start: `resumed: refresh staleness check` → `apps refreshed in 3354ms` (appsStale=true, measured).
  - Return after ~3.5 min (>60 s stale): `apps refreshed in 1470ms` (appsStale=true → refresh again).
  - Return within 60 s: guard correctly skips the refresh (appsStale=false / channelsStale=false).
  - `resumed: refresh failed: StandaloneCoroutine was cancelled` is pre-existing `repeatOnLifecycle` lifecycle-dip behavior (not a regression); the try/catch makes it harmless and observable.
  - Build: `:app:assembleDebug` + `:app:lintDebug` + `:app:testDebugUnitTest` green.
- Remaining validation or risk: The ~1–2 s Back-transition main-thread jank (`Davey!` frames) is a separately scoped task and was not addressed here. Manual TalkBack spoken validation remains pending. The `StandaloneCoroutine was cancelled` cancellation is pre-existing but worth confirming it doesn't affect focus stability in longer sessions.
- Next action: Scope and tackle the Back-transition jank task; manual TalkBack pass remains the accessibility milestone's last requirement.

## 2026-09-13 — Back-transition layout feedback loop removed

- Change or finding: The Back-transition jank was traced on the connected Chromecast (`sabrina`, Android 14) to debug-only placement counters. `AccessibilityOverviewScreen` and `FocusRestorationScreen` incremented Compose `mutableStateOf` values from `onPlaced`; each write requested another layout and another placement. The overview counter-only handler was removed. The detail screen keeps its placement-based entry-focus request, but no longer counts/logs every placement.
- Why it matters: The feedback loop competes with focus restoration on the main thread and causes severe frame loss, directly undermining blind users' expectation that Back returns them promptly and predictably.
- Files or area affected: `AccessibilityOverviewScreen.kt`; `FocusRestorationScreen.kt`.
- Evidence: Fresh device log on `sabrina` recorded `overview: onPlaced #36734` through `#36811` at approximately frame cadence, followed by `Davey! duration=1306ms`, `Skipped 87 frames`, and a second `Davey! duration=2072ms`. The updated debug APK installed successfully with `adb install -r`, and `nl.ndat.tvlauncher/.LauncherActivity` was top-resumed afterward. The generated lint report contains 14 warnings and no errors; the combined Gradle invocation did not exit because Kotlin build-statistics cleanup was scanning existing project cache/session files, so its final task status is not claimed.
- Remaining validation or risk: ADB input was routed through TalkBack and could not reliably replay the settings path after install. Run the physical-remote Back flow and confirm no `Davey!` or skipped-frame event, then record perceived delay and spoken wording. Manual TalkBack validation is still mandatory.
- Next action: Perform that physical-remote Back check on `sabrina`, then rerun the focused Gradle checks in a clean Gradle-user-home environment if a definitive command exit is required.

## 2026-09-13 — Orientation help accessibility page

- Change or finding: Added an Orientation help entry to Accessibility and a dedicated help page. It explains D-pad exploration versus activation, opening app options with a long press, and predictable Back recovery in concise language.
- Why it matters: A blind user can now learn the launcher interaction model from within the launcher without relying on visual hints or an external guide. The page deliberately has no automatic announcement or custom focus graph, avoiding duplicate speech and focus traps.
- Files or area affected: `Destinations.kt`, `AccessibilityOverviewScreen.kt`, `OrientationHelpScreen.kt`, and `strings.xml`.
- Evidence: A new debug APK was generated and `adb install -r` completed successfully on the only attached target, Chromecast with Google TV (`device:sabrina`). After an explicit launch, `LauncherActivity` was top-resumed (PID 26979); the hierarchy exposed Home, All apps, and Settings controls.
- Remaining validation or risk: Use the physical remote and TalkBack to open Orientation help, traverse each heading and paragraph, and press Back to confirm return focus and concise speech. This does not replace the milestone's required Back-transition and full manual TalkBack pass.
- Next action: Run the physical-remote TalkBack checklist, beginning with Orientation help and the Accessibility → Focus restoration → Back path.

## 2026-09-13 — Accessibility overview entry-focus restoration

- Change or finding: Restored the documented focus-restoration pattern on the Accessibility overview. Its `LazyColumn` now uses `focusRestorer()` and the Focus restoration card requests entry focus from `onPlaced` only until the request succeeds. The old debug placement counter remains removed.
- Why it matters: Entering or returning to Accessibility now has a defined, usable D-pad target instead of relying on default focus search to select the toolbar. The one-time state transition cannot recreate the layout feedback loop that caused Back jank.
- Files or area affected: `AccessibilityOverviewScreen.kt`.
- Evidence: A fresh debug APK was generated and installed successfully to Chromecast (`device:sabrina`). After explicit launch, `LauncherActivity` was top-resumed (PID 27514).
- Remaining validation or risk: With the physical remote and TalkBack, confirm entering Accessibility focuses Focus restoration, Back from Focus restoration returns there, and Back from Orientation help returns to a usable overview control without duplicate speech.
- Next action: Record that manual pass together with the post-fix Back-transition timing.

## 2026-09-13 — Discoverable app-card options

- Change or finding: App cards that provide an options popup now expose a labeled `App options` long-click accessibility action. Cards without a popup do not expose the action.
- Why it matters: The long-press menu is no longer an undiscoverable remote-only gesture. This follows Android's current TV TalkBack guidance to use accurate action descriptions and Compose guidance to expose labeled semantics for non-obvious interactions.
- Files or area affected: `AppCard.kt`, `ACCESSIBILITY_REQUIREMENTS.md`, and `TEST_CHECKLIST.md`.
- Evidence: Focused debug APK built and installed successfully to Chromecast (`device:sabrina`). `LauncherActivity` was top-resumed (PID 27979). The device hierarchy retained a single named `Just Player` semantic descendant; UIAutomator does not expose the screen reader's action-menu labels.
- Remaining validation or risk: With TalkBack, inspect a Home and All Apps card's action menu. Confirm `App options` appears only where a popup is available, opens the existing menu, is announced once, and Back returns focus to the invoking card.
- Next action: Complete the physical-remote TalkBack checklist and record the post-fix Back-transition timing.

## 2026-09-13 — Repeatable Chromecast Back evidence collection

- Change or finding: Added bounded Back-test collectors: `util/CollectChromecastBackEvidence.ps1` and a `.cmd` fallback for systems whose PowerShell execution policy blocks local scripts.
- Why it matters: It captures the exact performance/focus evidence needed for the current milestone while leaving spoken-wording validation to a person using TalkBack.
- Files or area affected: `util/CollectChromecastBackEvidence.ps1`, `util/CollectChromecastBackEvidence.cmd`, `CURRENT_MILESTONE.md`, and `TEST_CHECKLIST.md`.
- Evidence: PowerShell parser validation passed. Both collectors require one online device whose record contains `device:sabrina`; they clear only the transient ADB log buffer and filter the subsequent capture to `LauncherFocus`, `Davey!`, and skipped-frame events. The `.cmd` fallback's no-device path was checked without connecting to or altering a device.
- Remaining validation or risk: Run the collector while a tester performs the documented Accessibility → Focus restoration → Back and Orientation help → Back flows. Record actual focus, spoken wording, and any jank output.
- Next action: Use that evidence to complete the milestone's physical-TV validation.

## 2026-09-13 — Post-fix Back capture requires an idle Chromecast baseline

- Change or finding: The first physical-remote capture after removing the placement-state feedback loop still contained multi-second `Davey!` frames. The launcher frame summary reported 63 janky frames out of 65 (50th percentile 200 ms; 90th percentile 1,850 ms), but GPU 90th-percentile work was only 14 ms and package app-card asset reads were 4–5 ms. At that time the Chromecast had about 166 MB free RAM, 461 MB of 496 MB swap in use, and the background OTT Navigator process at 100% CPU.
- Why it matters: That device pressure can dominate UI-thread scheduling, so it is not valid evidence that the remaining delay is caused by launcher rendering. The placement counter was a real feedback-loop contributor, but it is not yet valid to call it the only Back-transition cause.
- Files or area affected: `CURRENT_MILESTONE.md`; both `CollectChromecastBackEvidence` collectors now clear logcat and reset launcher `gfxinfo` before capture, then record a `top` snapshot before and after the flow plus launcher `gfxinfo` with the filtered focus/frame log.
- Evidence: Physical-remote logcat on connected Chromecast (`sabrina`, Android 14); `dumpsys gfxinfo nl.ndat.tvlauncher`; `top -b -n 1`. No device settings or user applications were changed.
- Remaining validation or risk: A fair re-measurement needs the Chromecast otherwise idle, plus the required spoken TalkBack observations. If it remains janky under that condition, collect the new evidence before making another code change.
- Next action: Have the device owner close resource-intensive apps or reboot if they choose, then run `util\\CollectChromecastBackEvidence.cmd` with the physical remote and record the spoken wording and observed Back delay.

## 2026-09-13 — Current debug APK redeployed to Chromecast

- Change or finding: Redeployed the existing debug APK that contains the current accessibility changes to the connected Chromecast.
- Why it matters: The physical TalkBack and remote checks now run against the current launcher build on the intended hardware target.
- Files or area affected: Device deployment only; no source or system-settings changes.
- Evidence: Guarded ADB target inspection found exactly the Chromecast record `device:sabrina`; `adb install -r` returned `Success`. After an explicit start, `dumpsys activity` reported `nl.ndat.tvlauncher/.LauncherActivity` as top-resumed (PID 29539). Package metadata reports version `1.0.0` / version code `10000`. A subsequent `:app:assembleDebug` completed successfully in 49 seconds with all 39 tasks up to date.
- Remaining validation or risk: The physical TalkBack wording and fair idle-device frame capture remain required.
- Next action: With the Chromecast otherwise idle, run the `.cmd` collector while a person uses the physical remote for the two documented Back flows.

## 2026-09-13 — Refresh no-op guards and off-main artwork resolution

- Change or finding: App, channel, and channel-program repositories now compare refreshed content to the existing database rows and skip writes when identical. App-card artwork resolves on `Dispatchers.IO`; cards avoid issuing Coil requests until artwork is ready.
- Why it matters: A stale refresh that finds no real data change no longer invalidates SQLDelight flows, reconstructs TV rows, moves focus, or risks repeated TalkBack speech. Moving package artwork lookup off the main thread removes synchronous package-manager work from composition while retaining the card's immediate accessible name and action.
- Files or area affected: `AppRepository.kt`, `ChannelRepository.kt`, `AppCard.kt`.
- Evidence: After deployment to the guarded Chromecast target (`sabrina`), a settled stale return measured apps refresh at 295 ms and channels at 718 ms, with no `LauncherFocus` movement and a 200 ms worst sampled frame. An immediate follow-up return logged `appsStale=false channelsStale=false`. The prior cold process launch remained slow (median 2.25–2.95 s frames despite 5–15 ms GPU work), proving this change specifically protects return/refresh behavior rather than masking the separate cold-start issue. Current-source `:app:assembleDebug` and `:app:lintDebug` both completed successfully; lint reported 14 non-accessibility findings and no accessibility findings.
- Remaining validation or risk: Cold process startup is still dominated by debug-build/low-memory startup work. Test-only `cmd package compile -m speed -f nl.ndat.tvlauncher` improved the cold-frame median to 1.3 s but did not eliminate multi-second frames; do not treat that device-local compilation as a release fix. A baseline-profile release setup requires a separately approved architecture decision. Manual TalkBack wording and the physical-remote Back test remain required.
- Next action: Retain the installed build for the physical TalkBack/remote validation. Consider a baseline-profile module only after explicit approval.

## 2026-09-13 â€” Physical-remote Back flows passed

- Change or finding: The tester completed the physical Chromecast remote and TalkBack checks for Accessibility → Focus restoration → Back and Orientation help → Back, and reported both flows passed.
- Why it matters: These are the settings-return paths affected by the placement-based focus-restoration fix. A manual pass supplies the spoken and remote evidence that ADB cannot capture.
- Files or area affected: Validation only; no source change.
- Evidence: Tester report in the development session. The intended Chromecast target (`sabrina`) was connected and verified before the report.
- Remaining validation or risk: The report did not include a clean idle-device frame capture, measured delay, or verbatim TalkBack wording. The broader TalkBack checklist remains open, including Apps-grid flicker, last-favorite row-end behavior, and popup wording.
- Next action: Complete the remaining manual checklist paths; run the optional Back evidence collector only with the Chromecast otherwise idle if quantitative frame evidence is needed.

## 2026-09-13 â€” Quantitative Back capture on a pressured device

- Change or finding: A 20-second physical-remote collector run completed on `sabrina` after the 45-second capture exceeded the workspace command limit. The Accessibility overview entry requester was accepted on each observed overview return. The log also contains transitions from Orientation help to the Settings toolbar, but it cannot distinguish a focus loss from a second Back press in the captured interaction.
- Why it matters: The capture provides real focus and frame evidence, but does not justify a launcher-only performance conclusion.
- Files or area affected: `util/CollectChromecastBackEvidence.cmd` now parses `adb devices -l` directly for the strict `device:sabrina` target, avoiding its failed piped filter. Validation and documentation otherwise only.
- Evidence: 31 of 38 rendered frames were janky; 90th percentile was 950 ms, with `Davey!` frames of 985 ms and 1,714 ms. GPU 90th percentile was 11 ms, while swap was 479 MB of 496 MB used and the launcher/TalkBack were active CPU consumers.
- Remaining validation or risk: This is not an idle-device baseline. Re-run only when the Chromecast is otherwise idle. Manually retest one single Back from Orientation help, observing whether focus returns to the overview card before any further remote input.
- Next action: Establish an idle-device baseline before changing performance code; if a single Back demonstrably lands on Settings instead of Accessibility, scope a focused return-restoration fix.

## 2026-09-13 â€” Batch evidence collector made automation-safe

- Change or finding: The `.cmd` collector now parses `adb devices -l` directly for `device:sabrina`, accepts an optional capture duration as its second argument, and uses a loopback delay instead of interactive `timeout`.
- Why it matters: The fallback no longer fails its device filter or exits early when started without an interactive command prompt.
- Files or area affected: `util/CollectChromecastBackEvidence.cmd`.
- Evidence: A one-second invocation with the Android SDK ADB executable found `sabrina`, reset and printed all expected capture sections, and completed without the previous `Input redirection is not supported` error.
- Remaining validation or risk: The one-second run is a harness check only, not a performance measurement. A meaningful 20- or 45-second run still requires an otherwise idle Chromecast and physical-remote interaction.
- Next action: When the device is idle, run `util\CollectChromecastBackEvidence.cmd "C:\path\to\adb.exe" 20` while testing the two Back flows.

## 2026-09-13 â€” Repeat Back capture confirms CPU-side jank pattern

- Change or finding: A second 20-second physical-remote capture recorded 13 janky frames out of 14, with 600 ms at the 90th percentile and one 1,000 ms `Davey!` frame. GPU 90th percentile was only 14 ms.
- Why it matters: The slow frames are not explained by GPU work. The likely sources are main-thread scheduling, CPU work, or the device's persistently high swap use.
- Files or area affected: Validation and milestone documentation only.
- Evidence: `dumpsys gfxinfo` from the guarded `sabrina` capture; swap was 478 MB of 496 MB used before and after the run. The launcher was the primary active process at capture start, then CPU-idle after interaction.
- Remaining validation or risk: This is still not a clean-memory baseline, so it cannot attribute the jank solely to launcher code. The focus log did not cleanly demonstrate the two prescribed flows, so it also does not settle the single-Back Orientation-help question.
- Next action: Get a clean device baseline after the owner chooses to close resource-heavy apps or reboot. If jank remains, approve a separately scoped baseline-profile build/release infrastructure change or collect a focused main-thread trace before changing production code.

## 2026-09-13 â€” Post-reboot Back capture

- Change or finding: A 20-second capture after the Chromecast reboot still rendered 18 janky frames out of 18. The 90th percentile was 1,050 ms and the 95th percentile was 1,700 ms, while GPU 90th percentile was 13 ms.
- Why it matters: The problem persists after reboot and is therefore reproducible, but the low GPU time continues to point away from rendering as the primary bottleneck.
- Files or area affected: Validation and milestone documentation only.
- Evidence: Guarded `sabrina` capture. Swap was 390 MB of 496 MB used before the run and 402 MB afterward. The capture recorded an Orientation-help return that focused the Settings toolbar, but it did not record the requested Focus-restoration route, so the exact remote sequence is not sufficiently controlled to declare a return-focus regression.
- Remaining validation or risk: Run one controlled Orientation-help check: enter Accessibility, move focus to Orientation help, activate it, then press Back exactly once and wait. Confirm whether the Accessibility overview card or Settings toolbar receives focus. Performance attribution remains unresolved.
- Next action: Confirm that single-Back focus result. If it reaches Settings, implement a focused Accessibility-overview return restoration; otherwise, prioritize a main-thread trace or explicitly approved baseline-profile work.

## 2026-09-13 â€” Orientation-help single-Back return confirmed

- Change or finding: The tester performed the controlled path: enter Accessibility, focus and open Orientation help, press Back exactly once, then wait. Focus returned to the Accessibility overview.
- Why it matters: This clears the suspected Orientation-help return-focus regression. The toolbar focus in an earlier capture resulted from an uncontrolled remote sequence and does not justify a focus-code change.
- Files or area affected: Manual validation and documentation only.
- Evidence: Tester report from the physical Chromecast remote flow.
- Remaining validation or risk: Main-thread jank remains reproducible after reboot and is an independent performance investigation.
- Next action: Capture a focused main-thread trace or approve a separately scoped baseline-profile build/release infrastructure change before modifying performance-sensitive production code.

## 2026-09-13 â€” Bounded system trace collected

- Change or finding: Captured a 20-second `atrace` session during the physical Back flows with scheduler, input, view, graphics, window, and activity categories.
- Why it matters: It extends the frame metrics with scheduler evidence before considering production performance changes.
- Files or area affected: Device diagnostics and milestone documentation only.
- Evidence: The trace contained about 28 MB of events, including concurrent `kcompactd0`, Wi-Fi-driver (`dhd_*`), and CPU-governor scheduling. The available local environment has no trace processor, so the trace cannot establish a launcher-only hotspot.
- Remaining validation or risk: System activity and memory pressure remain confounders. No code change is justified from this capture alone.
- Next action: Either analyze a saved trace with Perfetto trace processor tooling, or explicitly approve baseline-profile build/release infrastructure as the next production-oriented performance experiment.

## 2026-09-13 â€” Saved Perfetto-compatible Back trace

- Change or finding: Captured and retained a compressed 20-second `atrace` artifact for the Back flows in the project-specific temporary trace folder.
- Why it matters: The trace can be opened in Perfetto to correlate main-thread scheduling with the observed slow frames.
- Files or area affected: Temporary diagnostic artifact only; no source change.
- Evidence: The guarded Chromecast capture completed and pulled a 1.4 MB compressed trace artifact.
- Remaining validation or risk: No browser surface is available in this environment, so the approved upload to Perfetto could not be performed. The trace has not been transmitted.
- Next action: Open the retained trace with Perfetto trace processor tooling or a browser-enabled session, then inspect the launcher main thread around the Back interactions.

## 2026-09-14 — Removed remaining placement hot-path diagnostics

- Change or finding: The accessibility overview and focus-restoration detail screens still incremented non-state counters and logged every `onPlaced` callback even though the earlier Compose-state feedback loop had been removed. Removed those counters and repeated logs while preserving the snapshot-guarded placement-based focus requests and their one-time acceptance logs.
- Why it matters: Placement runs on the main layout path. Repeated debug logging added avoidable work and noise to the exact Back/focus transition being measured, and contradicted the milestone record that the counters had been removed.
- Files or area affected: `AccessibilityOverviewScreen.kt`, `FocusRestorationScreen.kt`, and `docs/CURRENT_MILESTONE.md`.
- Evidence: `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` completed successfully. The debug APK installed successfully on the guarded Chromecast target `sabrina`; explicit launch left `LauncherActivity` top-resumed with no `AndroidRuntime` error. After waking the display, the launcher hierarchy contained 100 launcher nodes and exactly one focused node labeled `Home`.
- Remaining validation or risk: ADB D-pad input was intercepted by TalkBack/system navigation and opened Google Play, so it did not validate the Settings Back path. Spoken output, popup wording, Apps-grid flicker, last-favorite row-end behavior, app return, and Watch Next still require the documented physical-remote TalkBack pass. The saved system trace remains unanalyzed because no local Perfetto trace processor is installed.
- Next action: Complete the manual TalkBack and physical-remote checklist. Keep quantitative Back profiling separate and run it only while the Chromecast is otherwise idle.

## 2026-09-14 — Repeated-return focus soak passed

- Change or finding: Ran a bounded 10-cycle background/foreground soak by alternating between Android Settings and the launcher on `sabrina` without changing any setting.
- Why it matters: This directly exercises the lifecycle boundary associated with the earlier `repeatOnLifecycle` cancellation report and verifies that repeated returns do not lose or multiply D-pad focus.
- Files or area affected: Runtime validation plus `docs/CURRENT_MILESTONE.md` and `docs/DECISIONS.md`; no production source change.
- Evidence: Every cycle exposed exactly one focused launcher node labeled `Home`. `LauncherActivity` was top-resumed after the tenth return. Filtered `LauncherFocus` and `AndroidRuntime` logs contained no refresh failure, cancellation entry, or crash.
- Remaining validation or risk: UI hierarchy cannot prove TalkBack wording or timing. The soak always returned to the configured Home fallback, so the user-preference question for Back from a focused All Apps grid card remains open.
- Next action: Complete the remaining manual physical-remote TalkBack checklist, beginning with Apps-grid flicker and the preferred Back destination from an All Apps card.

## 2026-09-14 — Trace isolated draw work; Watch Next enables hardware bitmaps

- Change or finding: Decoded the retained compressed `atrace` locally and correlated scheduler slices with Android trace markers. Launcher main-thread runnable waits were tiny, but uninterrupted CPU slices reached 207.951 ms and aligned with `Choreographer#doFrame`, traversal, display-list recording, and animation. `ChannelProgramCard` was forcing every poster to a software bitmap despite doing no CPU-side pixel processing; removed that override so Coil can use hardware-backed images on supported Android versions.
- Why it matters: The evidence rules out scheduler starvation as the primary cause in the captured interval and identifies draw/animation CPU work as launcher-owned. Avoiding forced software posters removes unnecessary bitmap preparation and CPU-to-GPU texture uploads visible in the trace without changing navigation or accessibility behavior.
- Files or area affected: `ChannelProgramCard.kt`, `docs/CURRENT_MILESTONE.md`, and this log.
- Evidence: Trace statistics: 631 main-thread run slices, maximum 207.951 ms and 95th percentile 27.117 ms; 318 wake samples, maximum runnable wait 1.527 ms and 95th percentile 0.110 ms. `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` passed. The APK installed on guarded target `sabrina`; Watch Next was present with 100 launcher hierarchy nodes, one focused `Home` node, and no AndroidRuntime/Coil error. D-pad traversal produced one focused node at every step: `Home`, `Just Player`, then three meaningfully labeled program cards including season/episode/progress where available.
- Remaining validation or risk: The retained trace predates this hardware-bitmap change. A comparable physical-remote Back capture is required before claiming a measured frame-time improvement. TalkBack speech and timing still require the manual checklist.
- Next action: Run the physical Back-flow collector while the Chromecast is otherwise idle, then compare main-thread frame evidence. Complete the remaining TalkBack wording and navigation judgments separately.

## 2026-09-14 — Card focus enlargement removed after repeated A/B measurements

- Change or finding: Disabled focused scaling for app and Watch Next cards while retaining their 2 dp high-contrast focused borders. A separate experiment removing lazy-item placement animations was reverted because its device result was mixed.
- Why it matters: The saved trace points to CPU-heavy animation/display-list work. Focus enlargement is redundant when the focused border already communicates state, and repeated device measurements showed a consistent reduction in frame percentiles without changing focus latency or event completeness.
- Files or area affected: `AppCard.kt`, `ChannelProgramCard.kt`, `docs/CURRENT_MILESTONE.md`, and `docs/DECISIONS.md`. `AppsTab.kt` and `AppCardRow.kt` were restored after the rejected placement-animation experiment.
- Evidence: Default-scale 32-key workload: 43/43 janky frames, 50th/90th/95th/99th percentiles 950/1550/2700/4250 ms. No-scale runs: 48/48 at 750/1550/2500/4150 ms and 57/57 at 600/1400/2500/3900 ms. Focus latency remained 15–68 ms and all expected focus events were recorded; GPU work remained 6–15 ms. `:app:assembleDebug`, `:app:lintDebug`, and `:app:testDebugUnitTest` passed, and the retained APK installed successfully on `sabrina` without an AndroidRuntime error. A device screenshot with a Watch Next card focused showed the light border clearly separated from the dark background and adjacent cards, with no overlap.
- Remaining validation or risk: All frames remain classified janky, and the automated Home-row workload is not the manual Accessibility Back flow. A current-build trace is still needed to isolate remaining CPU work. User testing should confirm the border-only indicator meets individual low-vision preferences; TalkBack behavior is structurally unchanged but spoken validation remains pending.
- Next action: Capture a current-build trace around the physical Back flows or a tightly controlled automated row traversal, then correlate the remaining long CPU slices before changing more animation or drawing behavior.

## 2026-09-14 — Current-build trace narrows remaining focus jank

- Change or finding: Captured a new compressed `atrace` from the retained hardware-bitmap/no-scale build during the exact 32-key Home/Watch Next workload and analyzed it locally. Scheduler delay remains negligible, while main-thread Compose measurement, recomposition, animation, semantics processing, and lazy prefetch contain long CPU sections. A focused-title marquee A/B was built and measured twice, then reverted because tail latency and focus latency worsened despite a lower median.
- Why it matters: The evidence moves the investigation beyond the older build and rules out device scheduling, GPU execution, focused card scaling, and the title marquee as sufficient explanations. TalkBack semantics-tree work and lazy prefetch now have concrete timing evidence and should be isolated before another production change.
- Files or area affected: Retained diagnostic artifact `trace-temp/current-row-navigation.atrace` plus milestone documentation. The temporary `AppCard.kt` marquee experiment was fully reverted; retained production behavior is unchanged from the previously verified hardware-bitmap/no-scale build.
- Evidence: Current trace: 4,224 launcher-main run slices, maximum 324.324 ms and 95th percentile 32.148 ms; 928 runnable-wait samples, maximum 4.505 ms and 95th percentile 0.117 ms. Named maximums include `AndroidOwner:measureAndLayout` 1,535.75 ms, `Recomposer:animation` 615.30 ms, `Recomposer:recompose` 488.75 ms, lazy prefetch 323.56 ms, semantics-node collection 239.10 ms, and focus dispatch 216.64 ms. No-marquee runs lowered the median to 550/350 ms but raised 90th percentile to 2,000/2,100 ms and worst focus callbacks to 399/434 ms; the change was rejected. The restored retained source rebuilt successfully and the APK reinstalled on `sabrina`.
- Remaining validation or risk: The trace workload is automated and TalkBack-enabled but does not prove spoken timing. The named sections overlap and cannot yet attribute the long semantics cost to one app semantic node or distinguish Compose runtime cost from framework accessibility-service demand.
- Next action: Add narrowly scoped debug trace sections around app/program card composition and focus handling, or capture a matched TalkBack-on/off diagnostic if changing service state is explicitly authorized. Avoid production semantics changes until the responsible subtree is identified.

## 2026-09-14 — Program focus state no longer recomposes sibling cards

- Change or finding: `ChannelProgramCardRow` previously captured the current focused program in every card's `onFocusChanged` lambda. Every D-pad move therefore changed the modifier lambda for visible siblings and repeatedly entered their `AsyncImage` painter setup. Replaced it with a stable remembered state object and one remembered focus callback per program; only the details panel reads the focused value.
- Why it matters: The matched diagnostic trace identified program-card composition and Coil painter setup as a measurable main-thread focus cost. Stable callbacks reduce invalidation without changing the focused program, details panel, card semantics, D-pad order, or click behavior.
- Files or area affected: `ChannelProgramCardRow.kt`, `docs/CURRENT_MILESTONE.md`, and this log. Temporary debug trace wrappers were removed before the final build.
- Evidence: Card-labeled trace comparison for the same two-round Home/Watch Next workload: program cards 41 sections / 1,424.327 ms before versus 36 / 1,191.900 ms after; `rememberAsyncImagePainter` 53 / 491.904 ms versus 44 / 402.650 ms; `Compose:recompose` 257 / 4,639.950 ms versus 226 / 4,071.550 ms. Final `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` passed. The final APK installed on guarded `sabrina`; after waking and explicit launch, `LauncherActivity` was top-resumed with 100 launcher hierarchy nodes and exactly one focused `Home` node, with no AndroidRuntime error.
- Remaining validation or risk: Semantics-node collection remained variable and is still a substantial TalkBack-enabled cost. The trace was diagnostic and the automated hierarchy does not prove speech timing or the remaining manual navigation judgments.
- Next action: Measure a current clean build with the same workload before another production change; if semantics cost persists, identify whether it is driven by detail-panel updates or framework accessibility-service processing without weakening the single-action card semantics.

## 2026-09-14 — Detail state isolated from the program row

- Change or finding: Moved the focused-program state read into a dedicated restartable details composable and removed the unused `App` parameter from the row/details path. The card callbacks continue to write through the same stable state object.
- Why it matters: Changing the detail title and description on every D-pad move no longer invalidates the parent composition scope that owns the lazy program cards. This builds on the earlier stable-callback fix without changing card semantics, D-pad order, click behavior, or visible details.
- Files or area affected: `ChannelProgramCardDetails.kt`, `ChannelProgramCardRow.kt`, `HomeTab.kt`, `docs/CURRENT_MILESTONE.md`, and this log.
- Evidence: `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` completed successfully. The exact APK installed on guarded Chromecast target `sabrina`; `LauncherActivity` was top-resumed, the hierarchy contained 100 launcher nodes and exactly one focused node, and no crash occurred. After the repeated 32-key workload the focused card subtree exposed the `Just Player` accessibility label. The clean pre/post `gfxinfo` percentiles moved from 750/2000/2600/3900 ms to 600/1500/1600/1900 ms at p50/p90/p95/p99, while GPU percentiles were unchanged at 12/14/14/15 ms.
- Remaining validation or risk: ADB-driven frame metrics are noisy and every sampled frame is still classified as janky, so the percentile movement is directional evidence rather than a definitive benchmark. The hierarchy verifies focus ownership and labels, not TalkBack speech.
- Next action: Complete the remaining physical-remote TalkBack checklist. If performance work continues afterward, use a matched trace or Macrobenchmark/baseline-profile decision rather than further speculative UI simplification.

## 2026-09-15 — Focus-restoration regression coverage and launcher-state persistence foundation

- Change or finding: Made the focus-restoration and state-persistence utilities unit-testable, added regression tests, and laid the foundation for complete launcher-state restoration without wiring it into the UI yet.
  - Extracted a `PreferenceStore` interface with a `SharedPreferenceStore` implementation so `LastFocusedAppStore` and `AccessibilityPreferences` can be tested in-memory.
  - Converted `FocusRestorationManager` to depend on a `fun interface FocusRestoreModeProvider`; `AccessibilityPreferences` now implements that provider.
  - Added `LauncherStateSnapshot` (versioned JSON data class for tab, focused item id, scroll offsets, expanded sections) and `LauncherStateStore` backed by `PreferenceStore`.
  - Added `Destination` ↔ `SavedDestination` mapping and registered `LauncherStateStore` in Koin.
  - Added `FocusRestorationManagerTest`, `LastFocusedAppStoreTest`, `AccessibilityPreferencesTest`, `LauncherStateStoreTest`, and `DestinationMappingTest`.
- Why it matters: The backlog explicitly asks for focused regression coverage and complete launcher-state restoration. Covering the preference/fallback logic with fast unit tests protects the blind-user focus experience from regressions, and the versioned snapshot gives a safe place to grow state restoration without overpromising in the UI.
- Files or area affected: `util/PreferenceStore.kt`, `util/SharedPreferenceStore.kt` (new), `util/LastFocusedAppStore.kt`, `util/AccessibilityPreferences.kt`, `util/FocusRestorationManager.kt`, `util/LauncherStateSnapshot.kt` (new), `util/LauncherStateStore.kt` (new), `util/DestinationMapping.kt` (new), `LauncherApplication.kt`, `app/src/test/kotlin/nl/ndat/tvlauncher/util/*` (new tests + `InMemoryPreferenceStore`), and this log.
- Evidence: `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` completed successfully after each increment. Unit tests cover mode-to-target mapping, favorite-index fallbacks, last-focused-app store round-trip, preference migration and round-trip, state-snapshot default/round-trip/corrupt-json/clear, and destination mapping round-trips.
- Remaining validation or risk: The new `LauncherStateStore` is registered but not yet used by `LauncherScreen`; actual save/restore behavior is unchanged for users. `COMPLETE_LAUNCHER_STATE` remains honestly labeled as deferred/coming soon. No device validation was performed this turn.
- Next action: Decide whether to wire `LauncherStateStore` into `LauncherScreen` (gated behind `COMPLETE_LAUNCHER_STATE` or a debug flag), perform the remaining manual TalkBack/remote checklist, or commit the current uncommitted work.

## 2026-09-15 — Tab-level launcher-state restoration for COMPLETE_LAUNCHER_STATE

- Change or finding: Wired `LauncherStateStore` into the launcher so the active tab (Home or Apps) is persisted and restored, but only when the selected mode is `COMPLETE_LAUNCHER_STATE`. `FocusRestorationManager` gained `restoresLauncherState()`, `LauncherScreenViewModel` now resolves the startup destination and focus target from the saved tab, and `LauncherScreen` writes the current tab on every destination change. When the restored tab is Apps, the startup focus target is switched to `ALL_APPS_TAB` so focus does not wait on a favorites row that is not composed.
- Why it matters: This is the first real slice of the backlog's high-priority "complete launcher-state restoration" item. It keeps the deferred decision honest: only the tab is restored, and the mode description now says so instead of claiming full restoration.
- Files or area affected: `util/FocusRestorationManager.kt`, `util/DestinationMapping.kt`, `ui/screen/launcher/LauncherScreenViewModel.kt`, `ui/screen/launcher/LauncherScreen.kt`, `LauncherApplication.kt`, `res/values/strings.xml`, `app/src/test/.../FocusRestorationManagerTest.kt`, `DestinationMappingTest.kt`, `LauncherScreenViewModelTest.kt`, and this log.
- Evidence: `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` completed successfully. 32 unit tests pass, including 8 `LauncherScreenViewModelTest` cases covering non-restoring modes, `ALL_APPS_TAB`, saved-tab restore, settings-destination fallback, focus-target selection, and save behavior. The debug APK built successfully. Mode description updated to "Restores the last tab you used. Focused item and scroll position are coming soon."
- Remaining validation or risk: Only the tab is restored; focused item, scroll position, and expanded sections are not. Settings destinations are deliberately not restored as root entries because Back would not return to Home. No device or TalkBack validation was performed this turn.
- Next action: Extend restoration to focused item and scroll position, or run the deferred manual TalkBack/physical-remote checklist.

## 2026-09-15 — Focused-item state restoration for COMPLETE_LAUNCHER_STATE

- Change or finding: The launcher now records the last focused app card (Home favorites and All Apps grid) and restores D-pad focus to it on startup when the selected mode is `COMPLETE_LAUNCHER_STATE`. A `LauncherStateRecorder` singleton keeps destination and focused-item state in memory and flushes exactly once per foreground session in `LauncherActivity.onStop`, so focus-path state changes perform no I/O. `StartupFocus` gained an `itemId`; `AppsTab` retargets its entry focus requester to the saved card when present, and `AppCardRow` uses the saved favorite position for its entry requester, falling back to the existing preferred index when the card is no longer visible.
- Why it matters: This is the second slice of the backlog's high-priority "complete launcher-state restoration" item. A blind user relaunching the launcher returns to the same tab and the same card they left, which preserves orientation without any misleading wording.
- Files or area affected: `util/LauncherStateRecorder.kt` (new), `util/composition/startupFocus.kt`, `ui/screen/launcher/LauncherScreenViewModel.kt`, `ui/screen/launcher/LauncherScreen.kt`, `ui/tab/apps/AppsTab.kt`, `ui/tab/apps/AppsTabViewModel.kt`, `ui/tab/home/HomeTabViewModel.kt`, `ui/tab/home/row/AppCardRow.kt`, `LauncherActivity.kt`, `LauncherApplication.kt`, `app/src/test/.../LauncherStateRecorderTest.kt` (new) and `LauncherScreenViewModelTest.kt` (updated for the recorder API), and this log.
- Evidence: `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` completed successfully. 36 unit tests pass, including 4 `LauncherStateRecorderTest` cases (no writes before flush, flush persistence, repeated-record dedup, no-op flush) and 8 `LauncherScreenViewModelTest` cases covering startup item exposure per mode and flush-gated destination persistence. All 7 test suites reported 0 failures.
- Remaining validation or risk: Watch Next and provider-channel program cards do not record or restore focused items yet; scroll position and expanded sections remain unrestored. Only Home/Apps destinations and app-card focus are covered. No device or TalkBack validation was performed this turn.
- Next action: Consider scroll-position restoration for the lazy tabs, or run the deferred manual TalkBack/physical-remote checklist before claiming these paths on real hardware.

## 2026-09-15 — Scroll-position state restoration for the launcher tabs

- Change or finding: The Home `LazyColumn` and the All Apps `LazyVerticalGrid` now restore their first-visible item index on startup and record it on change through the same `LauncherStateRecorder` (new `recordScrollOffset`/`scrollOffset` API and per-tab keys `LauncherStateScrollPositions.HOME_TAB`/`APPS_TAB`). Recording stays in memory and lands on disk only in the existing `LauncherActivity.onStop` flush, so scrolling frames perform no I/O.
- Why it matters: A blind user returning to the launcher lands on the same part of a long list instead of being sent back to the top, which completes the tabPlus-focused-item restoration triplet (tab, focused item, scroll position) from the backlog's state-restoration item.
- Files or area affected: `util/LauncherStateRecorder.kt`, `util/LauncherStateSnapshot.kt`, `ui/tab/home/HomeTabViewModel.kt`, `ui/tab/home/HomeTab.kt`, `ui/tab/apps/AppsTabViewModel.kt`, `ui/tab/apps/AppsTab.kt`, and this log.
- Evidence: `:app:assembleDebug :app:lintDebug :app:testDebugUnitTest` completed successfully. LauncherStateRecorderTest grew to 6 cases covering in-memory-only recording, flush persistence, per-key overwrite, and unknown-key defaulting; all suites total 38 tests with 0 failures. Net effect: restoring a saved index re-creates the list state at that index, matching how the startup focus target re-anchors the focused card.
- Remaining validation or risk: Only the first-visible item index is restored, not the exact pixel offset; horizontal program rows and inner row positions are not restored. Index-based restoration combined with the focus restore chain keeps the focused card visible, but device behavior was not verified this turn.
- Next action: Run the deferred manual TalkBack/physical-remote checklist for the restored startup flows, or extend restoration to horizontal row positions.

## Entry template

```markdown
## YYYY-MM-DD — [short title]

- Change or finding:
- Why it matters:
- Files or area affected:
- Evidence: build, lint, test, device, and/or manual observation.
- Remaining validation or risk:
- Next action:
```
