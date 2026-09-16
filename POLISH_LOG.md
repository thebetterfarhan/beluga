# Accessibility polish log

## 2026-09-12 â€” Home focus restoration and validation

- **Issue found:** The Home tab's vertical scroll container did not retain its
  most recently focused child, unlike the All Apps grid and horizontal rows.
  Returning to Home after a tab change or data update could therefore fall back
  to Compose's default focus search.
- **Affected files:** `app/src/main/kotlin/nl/ndat/tvlauncher/ui/tab/home/HomeTab.kt`
- **Fix implemented:** Added Compose `focusRestorer()` to the Home `LazyColumn`.
  This preserves normal directional navigation while restoring the prior viable
  focus target when its subtree regains focus.
- **Debug logging:** The tab event now records both the source and destination,
  under the existing debug-only `LauncherFocus` tag.
- **Validation performed:** Built, installed, and explicitly launched the debug
  APK on a Chromecast with Google TV (Android 14). `LauncherActivity` was
  top-resumed after launch. The on-device accessibility hierarchy contained the
  Home and All apps tabs, a labeled Settings control, headings, and labeled
  program cards. TalkBack was enabled, but spoken output could not be captured
  through ADB. Manual spoken-output and full D-pad traversal remain required.
- **Regression check:** The combined local Gradle command was started after this
  change. Assembly produced the updated debug APK and lint completed with zero
  errors and nine pre-existing warnings; the runner did not return a final unit-
  test status, so no test result is claimed.
- **Remaining follow-up:** On a physical TV, follow
  `accessibility/TEST_CHECKLIST.md`, especially popup dismissal, Settings return,
  app-return restoration, and TalkBack wording.

## 2026-09-12 â€” Popup mutation timing

- **Issue found:** A favorite reorder could leave TalkBack/D-pad focus on an
  action that becomes disabled after the reorder. Favorite changes also left a
  stale popup visible while its backing data changed.
- **Affected files:** `AppCard.kt`, `tab/home/AppPopup.kt`,
  `tab/apps/AppPopup.kt`, `tab/home/row/AppCardRow.kt`, and `tab/apps/AppsTab.kt`.
- **Fix implemented:** Popup actions now dismiss their popup before changing
  favorite or order data. Existing popup-dismissal focus restoration then
  returns focus to the invoking card when it remains available.
- **Manual follow-up:** With TalkBack on, remove first, middle, last, and sole
  favorites; move a second item left and a penultimate item right; confirm one
  concise announcement, no stale popup, and a predictable remaining focus
  target. Also check Back from a focused All Apps card to Home, Settings return,
  app-return focus, and clock/list-refresh speech stability.

## 2026-09-12 â€” D-pad focus timing baseline

- **Instrumentation:** Debug-only `LauncherFocus` markers record one D-pad
  receipt at the Compose root and correlate a subsequent focus gain only within
  500 ms. This excludes asynchronous startup focus from navigation latency.
- **Measurement:** On the connected Chromecast with Google TV, after entering
  All Apps, D-pad Down focused the first app card in **33 ms** and D-pad Right
  focused the next card in **21 ms**. Each press produced one input marker and
  one focus marker; no duplicate focus callback was observed.
- **Conclusion:** These measured transitions are already below a perceptible
  navigation delay. No code-path optimization was made without a demonstrated
  bottleneck.
- **Manual follow-up:** Repeat with physical remote and TalkBack speech enabled
  to measure audible announcement onset; Android debug logs cannot reliably
  report speech synthesis timing.

## 2026-09-12 â€” Popup focus timing

- **Measurement:** Long-pressing an All Apps card focused the popup's first
  action in **229 ms**. Back dismissed the popup and restored D-pad focus to
  the invoking card.
- **Diagnostic refinement:** Android long-press dispatch supplied a repeated
  key-down event. The debug input marker now ignores repeat events so one
  physical press is measured once.
- **Manual follow-up:** Confirm the same popup timing and single spoken pane
  announcement with TalkBack's physical remote gesture.

## 2026-09-12 â€” Tab focus timing

- **Measurement:** On the connected Chromecast, D-pad Right moved focus from
  Home to All Apps in **15 ms**. The navigation event did not activate the tab,
  preserving the expected distinction between focus and selection.
- **Conclusion:** Toolbar tab focus is well below perceptible latency; no
  optimization is warranted from this measurement.

## 2026-09-12 â€” Settings focus timing

- **Measurement:** D-pad Right moved focus from All Apps to Settings in
  **17 ms** on the connected Chromecast.
- **Conclusion:** Toolbar edge navigation is immediate and predictable; no
  focus-path optimization is warranted from this measurement.

## 2026-09-12 â€” Accessibility-tree spot check

- **Validation:** The active Chromecast hierarchy contained exactly one focused
  node. Settings and All Apps each appeared once in the exposed tree.
- **Conclusion:** No duplicate focused node or duplicated toolbar label was
  observed in this snapshot. Spoken-event duplication still requires manual
  TalkBack verification because it is not represented by the UI hierarchy.

## 2026-09-12 â€” Back restoration manual check

- **Automated observation:** Scripted Back navigation returned to Home with
  LauncherActivity still top-resumed, but the resulting D-pad focus was
  Settings. The scripted hierarchy could not authoritatively prove that All
  Apps had been activated before Back.
- **Required manual check:** With TalkBack, activate All Apps, move to a known
  grid card, press Back once, and record the restored Home target and spoken
  announcement. Do not treat the scripted observation as a regression until
  this activation path is confirmed.

## 2026-09-12 â€” Verified All Apps Back path

- **Validation:** D-pad Left focused All Apps in **13 ms**; activation emitted
  the debug transition `Home -> Apps`. From the first All Apps card, Back kept
  LauncherActivity top-resumed and restored focus to the All Apps tab.
- **Follow-up:** Manually verify whether returning focus to the unselected All
  Apps tab is the preferred TalkBack workflow, or whether Home should receive
  focus. No automatic change was made because both can be defensible and the
  correct choice depends on blind-user testing.

## 2026-09-12 â€” Remember last focused app

- **Feature:** The launcher stores the last focused favorite app in private
  preferences and restores it on the next Home startup when it still exists.
  Missing or unfavorited apps safely fall back to the first favorite.
- **Validation:** On the connected Chromecast, `Just Player` was focused, the
  launcher was force-stopped and explicitly relaunched, and `Just Player` was
  again the focused Home card.
- **Performance refinement:** Duplicate focus writes are skipped and preference
  persistence runs on `Dispatchers.IO`, keeping card-focus handling off the UI
  thread. The refined build was installed and launched successfully on the
  connected Chromecast with LauncherActivity top-resumed.

## 2026-09-15 " Baseline profile for cold start

- **Goal:** Reduce launcher cold-process startup on the Chromecast, the
  milestone's remaining startup concern outside manual TalkBack validation.
- **Added:** `:baselineprofile` `com.android.test` module with a
  `BaselineProfileGenerator` (explicit cold start of `LauncherActivity`, initial
  Home tab focus/composition, brief D-pad traversal); `androidx.baselineprofile`
  plugin (1.5.0-alpha02) in `:app`; `profileinstaller` dependency; committed
  profile at `app/src/main/generated/baselineProfiles/baseline-prof.txt`; a
  generation-only `benchmark` build type.
- **Validation:** Profile collected on `sabrina` (Android 14) in five
  successful iterations. The release APK embeds `assets/dexopt/baseline.prof`;
  `assembleNonMinifiedRelease`, `lintDebug`, and `testDebugUnitTest` are green.
  After install, ProfileInstaller logged "Installing profile". Three
  force-stopped cold starts on-device measured `am start -W` TotalTime
  1,069/1,199/1,075 ms. A profile-less release baseline was not measured, so no
  improvement percentage is claimed.
- **Known issue:** The `:app:generateBaselineProfile` connected task exits
  nonzero although the on-device instrumentation completes successfully
  (JUnit XML shows 0 failures, all iterations `Status: ok`, profile file
  produced). The current workaround: copy
  `baselineprofile/build/outputs/connected_android_test_additional_output/.../baseline-prof.txt`
  to `app/src/main/generated/baselineProfiles/baseline-prof.txt`. Reported in
  `docs/DECISIONS.md` with a revisit condition.
- **Manual follow-up:** None new; existing TalkBack checklist unchanged.

## 2026-09-15 " Baseline profile diagnostics and follow-ups

- **Generation re-verified:** A second `:app:generateBaselineProfile` run on
  `sabrina` completed five stable iterations ("Profiles stable in iteration 5
  (for 2 iterations)", `speed-profile` dexopt performed on-device), startup
  measured inside the profiled scope was 568 ms, and a fresh
  `baseline-prof.txt` was pulled into Gradle's additional-output directory.
- **Exit-code quirk confirmed reproducible:** The connected task still exits
  nonzero although the on-device JUnit XML shows zero failures, all iterations
  `Status: ok`, and the profile file is pulled and APKs uninstalled cleanly.
  AGP applies "There were failing tests" purely from
  `test-result-exit-code.txt = 1`. Diagnosis attempted via `--info` rerun; no
  on-device error surface was found. Appears to be a benchmark
  1.5.0-alpha02/AGP 9 internal-test-task interaction; the generated profile
  itself is valid. Workaround unchanged (manual copy into
  `app/src/main/generated/baselineProfiles/`).
- **Profile-less baseline comparison was attempted and abandoned:** moving
  `app/src/main/generated/baselineProfiles/baseline-prof.txt` out and
  rebuilding still produced a `baseline.prof` embed. The merge step consumes
  library-provided profiles from dependency AARs (Single
  `merged_art_profile` output contained only dependency classes plus 379
  launcher-class lines that persist from the plugin's own captured output).
  A clean profile-less launch APK could not be constructed through the plugin
  alone; `/s`earch of the tree showed no other committed profile file. A
  targeted quantitative comparison requires disabling the plugin or a
  dedicated no-profile variant, neither of which is worth build
  infrastructure now. Current release cold-start numbers with the profile:
  1,069/1,199/1,075 ms (first session) and 1,087/1,268/934 ms (re-verified).
- **Manual follow-up:** None new. The existing TalkBack/physical-remote
  checklist remains the accessibility blocker.

## 2026-09-15 " Google TV launcher management card

- **Feature:** The Settings (Accessibility) overview now offers a
  "Google TV launcher" card. It reports the stock launcher's enabled/disabled
  state in the merged content description ("Google TV launcher. Disabled.
  Open system app settings to disable or enable the Google TV launcher.") and
  activation opens the system App-Info page for
  `com.google.android.apps.tv.launcherx`.
- **Constraint:** A regular app cannot change another package's enabled state;
  that requires device-owner or system privileges (this provisioned Chromecast
  cannot be granted device-owner). The card therefore delegates the actual
  toggle to the system App-Info page, which the system itself guards with
  confirmation. State detection is `PackageManager.getApplicationEnabledSetting`;
  the visible Current line and spoken state refresh on every app RESUMED via a
  `LifecycleEventObserver` (no timers, no focus churn).
- **Files:** `util/GoogleTvLauncherHelper.kt` (new),
  `ui/screen/accessibility/AccessibilityOverviewScreen.kt`, `strings.xml`.
- **Validation on `sabrina`:** hierarchy check found the card with one merged
  name/state/action; activation (D-pad center) opened
  `com.android.tv.settings/.device.apps.AppManagementActivity`; Back returned
  to `LauncherActivity` with focus restored on the same card. `assembleDebug`,
  lint, and unit tests are green.
- **Manual TalkBack follow-up:** spoken wording of the card and the return
  after Back still require a manual TalkBack pass (hierarchy cannot prove
  speech). Recorded in the TalkBack test plan scope.
- **Home routing note:** role ownership and package disabling on the connected
  device were performed via adb at the user's explicit request; no launcher
  code intercepts Home routing.

## 2026-09-15 " ADB-driven TalkBack checklist pass (hierarchy-level)

- Ran the `TALKBACK_TEST_PLAN` core checklist on `sabrina` with TalkBack
  enabled and ADB-driven D-pad steps, verifying the accessibility hierarchy
  after each input (same evidence level as prior sessions):
  cold-start focus restore, toolbar traversal (13-29 ms per hop, symmetric,
  edges stop), tab focus/activation separation, double-activation no-op,
  settings entry/Back restoration, popup open/Back with focus return to the
  invoking card, favorite add/remove round trip, app launch from the grid and
  HOME return with restored focus, Watch Next row-end edge stopping on the
  last labeled card, and the Google TV launcher management card.
- All recorded flows passed at the hierarchy level; `LauncherFocus` debug logs
  confirm single dispatch per action (one `tab changed`, single `popup
  dismiss`, no duplicated focus-callback pairs).
- Speech output remains unverifiable through ADB; wording, duplication and
  announcement timing require the manual physical-remote TalkBack pass that
  closes this milestone. The `TALKBACK_TEST_PLAN.md` result table records each
  flow's actual observations.

## 2026-09-15 " Google TV launcher card: honest state and recovery guidance

- **Device investigation (root cause of "cannot disable again"):** After the
  stock launcher is re-enabled, the launcher's HOME intent filter carries
  `priority=2`, so the system's default-resolution always picks the Google TV
  launcher for the Home press even when the HOME role, `pm set-home-activity`,
  the Default-home chooser selection, and role/controller records all report
  this launcher as the holder (`cmd package query-activities` showed
  `priority=2` vs `priority=0`). The system App-Info page for the stock
  launcher offers no Disable action on Android TV at all, and no in-app
  mechanism (device-owner included, since this provisioned device already has
  10 accounts) can change another package's enabled state.
- **Fix:** The Settings "Google TV launcher" card now distinguishes three
  states: Disabled, Enabled, and "Enabled and it owns the Home key" (detected
  via `PackageManager.resolveActivity(MAIN + CATEGORY_HOME, MATCH_DEFAULT_ONLY)`).
  It speaks a single merged description each time. WhenDisabled: activation
  opens the system App-Info page (Enable is the available action there). When
  enabled-but-owning-Home: activation opens the system Default-home chooser,
  and the description carries the honest instruction to run
  `adb shell pm disable-user --user 0 com.google.android.apps.tv.launcherx`
  because the system page cannot re-disable the stock launcher.
  Manifest home-primary intent-filter priority was attempted (50) but Android
  strips it for non-system packages, so no in-app ownership override exists.
- **Validation on `sabrina`:** Label verified in the hierarchy for every state;
  activation opened the Default-home chooser as expected in the
  owns-Home state; role/disabler adb route re-applied after testing and Home
  resolves back to `nl.ndat.tvlauncher`.
- **Remaining limitation (recorded in `docs/DECISIONS.md`):** full toggle with
  D-pad alone is impossible without device-owner privileges; the app's
  responsibility is to state the situation honestly and give the correct
  recovery command. Manual TalkBack wording pass pending.

## 2026-09-15 " Back-from-Apps no longer lands on the Settings icon

- The launcher-toolbar row had a `focusRestorer()` whose scope included the
  tabs and the Settings icon together; the most-recent focused descendant was
  often the Settings gear from prior interactions, so Back from All apps to
  Home restored focus to [1593,46] (the gear) instead of a Home-related
  target.
- **Fix:** removed the toolbar-level `focusRestorer()`. Tab-row focus
  restoration now lives only in `ToolbarTabs`, so Back into Home restores the
  All apps tab (the tab that was active) and a D-pad user lands back where
  they meant to be. Settings is still reachable via DPAD_RIGHT, but it no
  longer leaks into the Home-back restore path.
- **Validation on `sabrina`:** `pm clear` ? cold launch to Home (focus on the
  Settings button by default), DPAD_LEFT ? All apps tab, DPAD_CENTER ?
  Apps grid, BACK ? focus returned to All apps tab on Home, and a second
  Apps/Back round trip landed back on All apps tab consistently. Unit tests,
  lint, and `assembleDebug` are green.
- The earlier "settings-overview entry focus accepted=true" log on first
  launch is unchanged: that entry focus is the standard for the Settings
  page itself, not the Home toolbar.

## 2026-09-15 " Focus-restore dialog: collapse inner-text duplication, drop redundant heading

- Hierarchy dump and a mental run of TalkBack showed the dialog announced every
  option's description twice (once as the merged `contentDescription`, once as
  the inner `Text` node inside the card) and the page title as both heading
  and pane title, an accessibility-charter "no duplicate announcements" issue.
- **Fixes in `FocusRestorationScreen`:**
  - The merged card semantics now hold `contentDescription = name`,
    `stateDescription = description`, `selected`, and `Role.RadioButton`.
    Description lives in `stateDescription` (the canonical Compose slot for
    value-style text TalkBack reads after the role).
  - The visible inner `Text` nodes use `Modifier.clearAndSetSemantics {}` so
    the title and description stay on-screen for sighted users without being
    re-announced by TalkBack.
  - The first `Text("Focus restoration")` no longer carries `semantics
    { heading() }`; the LazyColumn's `paneTitle` already exposes the page
    title to TalkBack, so the heading was a duplicate.
  - The `debugLauncherLog` for per-option focus events now only fires when
    `isFocused` becomes true (skips blips).
  - Added `focusRestorer()` to the LazyColumn so Back-into-page focus behaves
    consistently with the Accessibility overview.
- **Strings:** replaced the unused `focus_restore_selected` resource with
  `focus_restore_option_state_selected` / `focus_restore_option_state_unselected`
  in case future code needs them; the current dialog relies on the
  `Role.RadioButton + selected` semantics, not these strings.
- **Validation on `sabrina`:** hierarchy dump now shows one `content-desc` per
  card matching its name with no description duplication. The page-title
  heading is no longer a separate focusable node. Entry focus lands on the
  currently selected mode (`Last focused app`).

## 2026-09-15 " Secondary accessibility pass

- **ChannelProgramCardDetails** — title and description `Text`s were not
  focusable or visible-only to sighted users, but TalkBack could still read
  them as separate text nodes while the focused channel card above already
  announced the same title and description through the program card's
  `contentDescription`. Applied `Modifier.clearAndSetSemantics {}` so the
  details panel stays visible while remaining silent to TalkBack.
- **Home AppPopup focus-routing bug** — the home-variant `AppPopup` carries
  three IconButtons (move-left, favorite, move-right). The previous code
  attached `firstActionModifier` to the move-left button when the item was
  not first and to the favorite button when it was first, so two icons could
  carry the focus-requester and only the favorite reliably accepted. The
  parameter is now renamed `favoriteFocusModifier` and always routes to the
  favorite IconButton; move-left/move-right continue to dismiss the popup
  but stay out of the popup's entry focus path. Same effect is what the
  apps-variant already had.
- **OrientationHelpScreen heading-vs-paneTitle duplication** — same root
  cause as the Accessibility overview earlier: the page title carried both
  `heading()` semantics and the LazyColumn's `paneTitle`. The redundant
  `heading()` was removed so TalkBack only speaks the title once on entry.
  Section subtitles keep their `heading()` so navigation by heading still
  jumps between sections.
- **Validation:** `assembleDebug`, `lintDebug`, and `testDebugUnitTest`
  green; fresh install on `sabrina` confirmed Orientation help has only
  the panel-title announcement and the App-card popup opens with the
  favorite IconButton focused.

## 2026-09-15 " Audit pass: NPE path in App.createDrawable and a comment typo

- **Bug: `App.createDrawable` could throw `NullPointerException`**. The `Int.parseUri(launchIntentUriLeanback ?: launchIntentUriDefault, 0)` call threw NPE when *both* stored intent URIs were null. Both columns are nullable in `App.sq`, so the database row could legitimately have both null. Guard now:
  - empty-or-null URIs fall back to `PackageManager.defaultActivityIcon`,
  - parse errors and `NameNotFoundException` are caught,
  - the original success path (banner then icon) is preserved.
- **`HomeTabViewModel.favoriteApp` had a comment typo** ("Return is state is already satisfied" ? "Skip if state is already satisfied").
- Reviewed the remaining low-risk files and found no additional crashes:
  - the favorites SQL (`updateFavoriteOrder`) is correct — verified by tracing a move-right against a 0,1,2,3 sequence and confirming the result fills the new slot without leaving a gap;
  - `commitChannels` / `commitChannelProgram` early-exit equality uses default data-class equals, which is the intended behavior because every field is part of the resolver output;
  - `RefreshStalenessTracker` is sound (`SystemClock.elapsedRealtime()` is always positive after boot, and the unit test that runs covers the boundary).

## 2026-09-15 " PackageChangeReceiver: replace `GlobalScope` with structured scope

- **Anti-pattern found**: `PackageChangeReceiver.onReceive` launched IO work
  on `GlobalScope` with an `@OptIn(DelicateCoroutinesApi::class)` opt-in.
  `GlobalScope` is a discouraged construct (unsupervised fire-and-forget,
  no cancellation, breaks structured concurrency).
- **Fix**: each broadcast creates a dedicated `CoroutineScope(SupervisorJob()
  + Dispatchers.IO)`. The receiver still calls `goAsync()` to acquire a
  `PendingResult`, finishes it (and cancels the scope) inside the same
  `try { … } finally { … }` block. Errors are silently swallowed so a
  failed database read does not crash the receiver; the next resume or
  broadcast will retry.
- **Validation**: `assembleDebug`, `lintDebug`, and `testDebugUnitTest`
  green. Live broadcast of `android.intent.action.PACKAGE_CHANGED` from
  the shell user is denied by the platform (BroadcastProtected), so the
  on-device path was exercised only via the existing `pm clear` /
  reinstall flows tested earlier in this milestone.
- **Reviewed and *deliberately not fixed*** (recorded here so future scans do not
  re-open them):
  - `commitChannel`/`commitChannelProgram` early-exit default-data-class `==`
    — every column comes from the resolver, so re-committing on any change is
    intended.
  - `commitChannel` compares the whole data-class including transient fields
    — the `App` analogue uses a hand-written `hasSameResolvedContent`
    because `favoriteOrder` is user-mutable and should not trigger
    re-commits.
  - `RefreshStalenessTracker` returns false on first run (the
    `lastSuccessfulRefreshElapsed = 0` case).
  - The favorites SQL (`updateFavoriteOrder`) keeps the order dense on a
    move — verified by paper-trace on a `0,1,2,3` sequence.

## 2026-09-15 " Cleanup pass: unused strings, redundant KDoc

- **Removed unused string resources.** Four entries were defined but never
  referenced anywhere in the Kotlin sources:
  - `settings_system` (a marker string from an older settings plan)
  - `settings_launcher`
  - `focus_restore_option_state_selected`
  - `focus_restore_option_state_unselected`
- **Trimmed redundant KDoc on `Modifier.ifElse`.** The class-level comment said
  only "Used to apply modifiers conditionally" — pure duplication with the
  function signature and behavior. Removed; the function still documents its
  overloads by signature.
- **Verified** by grepping the project for the four removed names: zero
  references, build green (`assembleDebug` + `lintDebug` + `testDebugUnitTest`).
- **No semantic behavior changes** for end users.

## 2026-09-15 " Throw error class name when refresh fails

- Refresh failures in `LauncherActivity.onCreate`'s `repeatOnLifecycle(STARTED)`
  block were logged with only `err.message`, which is null for many exceptions
  (`IllegalStateException`, `SQLiteException`, `PackageManager.NameNotFoundException`,
  `CancellationException` is rethrown but its message is empty). The log line
  now includes the class name and falls back to "(no message)" so the on-device
  `adb logcat -s LauncherFocus` capture always identifies the failure.
- Build + tests + lint green.
- No behavior change visible to users; only fixes a diagnostic gap on
  intermittent repository errors.

## 2026-09-15 " Home tab hides empty Favorites section

- A "Favorite apps" *heading* was always rendered on the Home tab, even when
  the favorites list was empty, exposing a focusable-but-content-less
  section that blind users had to traverse past.
- `HomeTab.kt` now conditional-skips the row when `apps.isEmpty()` (and the
  Watch Next row when its programs list is empty). The favorites heading
  reappears as soon as the user adds any favorite.
- **Verified on `sabrina`:** after `pm clear`-clean launch, the Home tab's
  text nodes are only `Home`, `All apps`, the clock, and `Watch Next` —
  the "Favorite apps" heading is gone. `assembleDebug`, `lintDebug`, and
  `testDebugUnitTest` are green.

## 2026-09-15 " Navigation backstack survives config changes

- `ProvideNavigation` previously defaulted its `backStack` to `remember { … }`,
  and `LauncherScreen` did the same. Configuration changes (rotation,
  density change, locale swap, theme swap) recreate the Composable tree,
  dropping the backstack to `[startupDestination]` — so BACK left the
  settings page and exited the launcher instead of returning to Home.
- The destination objects are `@Serializable` singletons, so the backstack
  can be persisted as a list of class names. Added
  `DestinationListSaver: Saver<SnapshotStateList<Destination>, Any>` in
  `util/composition/navigation.kt` and used `rememberSaveable(saver = …)`
  in `LauncherScreen` and `ProvideNavigation`. Restore silently drops
  unknown entries to keep state forward-compatible if new destinations
  are added in future.
- **Verified on `sabrina`:** cold launch lands on Accessibility and the
  Settings page renders cleanly. `assembleDebug`, `lintDebug`, and
  `testDebugUnitTest` green.
- The actual config-change case (rotation etc.) wasn't reproduced on the
  TV because Android TV doesn't rotate by default — the impact is rare
  in practice, but the fix removes the latent regression.

## 2026-09-15 " NPE path in card onClick launchers

- Both `AppCard.onClick` and `ChannelProgramCard.onClick` called
  `Intent.parseUri(uri, 0).startActivity` without a try/catch. A malformed
  stored URI raises `URISyntaxException` (subclass of `Throwable`) and
  crashes the launcher. While the `null` check guarded the explicit
  nullable fields, the encode/parse step has no defense.
- Wrapped both call sites in try/catch and log a debug line. Add the
  `debugLauncherLog` import that was previously missing on `AppCard`.
- Verified on `sabrina`: cold launch + install + foreground without
  crashes (the standard launch path uses well-formed URIs; the new
  safety net covers the "TV content providers push a malformed intent"
  edge case). Build + tests + lint green.
