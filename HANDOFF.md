# Handoff — TV Launcher

> **Read this first** if you're returning to this repo after a break, picking
> it up from someone else, or trying to ship a release.

## What this is

An **Android TV launcher** (`nl.ndat.tvlauncher`) **built blind-first** — every
enabled feature must remain fully usable with a D-pad and the supported
Android TV screen reader. Compose TV Material on top of a single-Activity
architected with Koin (DI), SQLDelight (data), Hilt-free Compose navigation,
and a generated baseline profile. No external backend.

## Build & run

JDK 21 (Gradle toolchain). Android Studio Hedgehog+ with AGP 9.4.0.

```
./gradlew :app:assembleDebug                    # build
./gradlew :app:testDebugUnitTest                # JVM unit tests
./gradlew :app:lintDebug                       # lint (debug variant)
./gradlew :app:installDebug                    # adb install
./gradlew :app:assembleNonMinifiedRelease      # release-like APK
./gradlew :baselineprofile:generateBaselineProfile   # profile on a device
```

The launcher targets `sabrina`-class Chromecasts but works on any Android TV
device with API 23+. The baseline-profile setup is intended for the
Chromecast generation; on other TV boxes re-generate or skip.

## Repo layout

```
app/                     # the launcher application module
  src/main/kotlin/...    # Kotlin sources
  src/main/sqldelight/   # SQLDelight .sq files
  src/test/              # JVM unit tests
baselineprofile/        # androidx.baselineprofile generator module
build.gradle.kts        # root build files
settings.gradle.kts
gradle/libs.versions.toml
docs/                    # project documents (see "Documents" below)
accessibility/           # accessibility team documents (committed)
AGENTS.md                # engineering agent guide
POLISH_LOG.md            # chronological session polish notes
HANDOFF.md               # this file
```

## Branch & remote state at handoff time

The branch in use is **`fix/talkback-navigation`** with HEAD
`7c1172d`:

```
$ git log --oneline -3
7c1172d  Performance + Google TV launcher card + accessibility and robustness polish
8b7c84f  Update androidx.compose to v1.12.1
…
```

Three remotes configured:

| Remote | URL | Pointer |
|---|---|---|
| `origin` (your fork) | `github.com/thebetterfarhan/tv-launcher.git` | HEAD = `7c1172d` (only on `fix/talkback-navigation`) |
| `upstream` (original) | `github.com/nielsvanvelzen/tv-launcher.git` | HEAD = `8b7c84f` |
| bare clones on the same machine | n/a | both `master` references also `8b7c84f` |

There are **no divergent commits** between your `master`, `origin/master`,
and `upstream/master`. All the work lives on `fix/talkback-navigation`. When
you push, only that branch is updated.

To push:
```
git push origin fix/talkback-navigation
```

To open a PR upstream:
```
gh pr create --base main --head fix/talkback-navigation \
  --title "Performance + Google TV launcher card + accessibility and robustness polish" \
  --body-file .github/PULL_REQUEST_TEMPLATE.md
```
If `gh` isn't authenticated in this environment, open the PR manually at
<https://github.com/thebetterfarjan/tv-launcher/compare/main...fix/talkback-navigation>.

## What was just shipped (commit `7c1172d`, ~60 files, +18k/−292)

### Performance
- `androidx.baselineprofile:1.5.0-alpha02` plugin, plus a `com.android.test`
  generator module (`:baselineprofile`) that drives a cold-start benchmark.
- Generated `app/src/main/generated/baselineProfiles/baseline-prof.txt` is
  committed (`saveInSrc=true`, `mergeIntoMain=true`) so release builds pick
  it up without re-running on a device.
- Verified cold-start at **~1.07–1.27 s** on the connected Chromecast.
  See `docs/POLISH_LOG.md` for the device-side timing logs.

### Device routing
- Settings → Accessibility → new card: **"Google TV launcher"** with three
  states (Disabled / Enabled / Enabled-and-owns-the-Home-key).
- Home-routing note: on Android TV, once the stock launcher is enabled, it
  pins Home to itself despite role/preferred-record preferences (its intent
  filter has `priority=2` and the system strips non-system filter
  priorities). The card honestly tells the user that re-disabling the
  stock launcher requires `adb shell pm disable-user
  --user 0 com.google.android.apps.tv.launcherx`. **There is no
  in-app-only path.**

### Accessibility
- Focus-restoration dialog cards: single `contentDescription = name`,
  `stateDescription = description`, `selected`, `Role.RadioButton`. Visible
  inner Text uses `Modifier.clearAndSetSemantics {}` so sighted users see
  title + description but TalkBack does not announce them twice.
- `AccessibilityOverviewScreen` and `OrientationHelpScreen`: dropped
  redundant `heading()` on the page title (the LazyColumn's `paneTitle`
  already announces it on entry). Section sub-headings keep `heading()` so
  navigation by heading still jumps between sections.
- `Toolbar.kt`: removed the toolbar-level `focusRestorer()` that previously
  captured the most-recent focused descendant overall (Back from All-apps
  → Home restored focus to the Settings gear). Tab-row focus is now scoped
  to `ToolbarTabs` only.
- `HomeTab`: hide the **Favorites** row when `apps.isEmpty()` and the
  **Watch Next** row when `watchNextPrograms.isEmpty()` — previously the
  headings were rendered even with no content, leaving a hollow focusable
  heading on Home.
- `HomeTab.AppPopup`: `favoriteFocusModifier` (renamed from
  `firstActionModifier`) routes to the favorite IconButton only — move
  buttons no longer compete for the entry focus.
- `ChannelProgramCardDetails` Text nodes use `clearAndSetSemantics {}` so
  the watched-row focus already carries the program info.

### Robustness
- `App.createDrawable`: null-safe URI handling plus caught exceptions,
  closing the `Intent.parseUri(null, 0)` NPE path.
- `PackageChangeReceiver`: replaced `GlobalScope` with a per-receive
  `CoroutineScope(SupervisorJob() + Dispatchers.IO)`. `pendingIntent.finish()`
  and `scope.cancel()` are tied inside `try { … } finally { … }`.
- `ProvideNavigation` / `LauncherScreen`: the navigation backstack uses
  `rememberSaveable(saver = DestinationListSaver)` (Destinations are
  `@Serializable`). Configuration changes no longer reset the backstack and
  pop the user out of the launcher when BACK is pressed in a settings page.
- `AppCard.onClick` and `ChannelProgramCard.onClick`: wrapped
  `Intent.parseUri(uri, 0).startActivity(...)` in `try/catch`. A malformed
  stored URI now logs a warning instead of crashing.

### Cleanups
- `HomeTabViewModel.favoriteApp`: removed typo comment.
- Removed unused string resources (`settings_system`, `settings_launcher`,
  `focus_restore_option_state_selected`,
  `focus_restore_option_state_unselected`).
- Trimmed redundant KDoc on `Modifier.ifElse`.
- `.gitignore`: ignore agent artefacts (`.gradle-codex-*`, `.android-codex/`,
  `util/`, `trace-temp/`) and per-run working artefacts (the
  `back-transition.atrace` etc. files are gitignored even if they reappear).
- `LauncherActivity`'s refresh-failure log now includes the exception class
  name plus a `(no message)` fallback.

### Documents updated
- `AGENTS.md` (project authoring guide).
- `POLISH_LOG.md` (chronological session notes).
- `docs/CURRENT_MILESTONE.md`, `docs/POLISH_LOG.md`, `docs/DECISIONS.md`.
- `accessibility/ACCESSIBILITY_REQUIREMENTS.md`.

## Tests

JVM unit tests at `app/src/test/kotlin/nl/ndat/tvlauncher/`:

- `AccessibilityPreferencesTest`
- `DestinationMappingTest`
- `FocusRestorationManagerTest`
- `LauncherScreenViewModelTest`
- `LauncherStateRecorderTest`
- `LauncherStateStoreTest`
- `LastFocusedAppStoreTest`
- `RefreshStalenessTrackerTest`
- Plus `InMemoryPreferenceStore` test fixture.

```
./gradlew :app:testDebugUnitTest
```

There is **no instrumented-test layer** (no Android connected tests yet).
Adding `:baselineprofile` also exposed a UI test scaffold but the
generator is left as a manual one-off until the `:app:generateBaselineProfile`
exit-code is fixed upstream.

Coverage gap to flag in a future round:

- `GoogleTvLauncherHelper` (resolveActivity vs role) — Android-instrumented
  test would be required because the helpers depend on `PackageManager`.
- `ProvideNavigation`'s `DestinationListSaver` round-trip — easy JVM test,
  just adds plumbing.
- `App.createDrawable` and the `Intent.parseUri` try/catch on the cards —
  simple JVM unit coverage for the null-safety and the catch paths.

## What is *not* shipped (open work)

These are intentional, not regressions.

- **Manual TalkBack + physical-remote pass.** This is the milestone's
  actual exit criterion and can't be scripted. See
  `docs/TALKBACK_TEST_PLAN.md` for the checklist. The ADB-driven equivalents
  (hierarchy dumps, launcher-focus logs) are recorded in
  `docs/POLISH_LOG.md`, but speech itself is out of reach for ADB.
- **Baseline-profile effort.** `:app:generateBaselineProfile` exits nonzero
  after a fully successful on-device run (current AGP-9 / benchmark-1.5.0
  combination). A manual copy workaround is recorded in `docs/DECISIONS.md`.
  Either upgrade the plugin once a stable fix lands upstream, or run the
  profile manually and check in the regenerated `baseline-prof.txt`.
- **Profile-less cold-start baseline** — the comparison number we don't
  yet have. The `APPS` grid measurement code in `docs/CURRENT_MILESTONE.md`
  sketches the path; not blocking.
- **Device-side quirks** every operator needs to know:
  1. On this provisioned Chromecast the launcher holds the HOME *role*
     (granted), and the system home press still routes through the role
     holder. **That worked once until we re-enabled the Google launcher
     for testing.**
  2. **Re-enabling the Google launcher** pins Home back to it (its
     priority-2 filter beats our preferred-home record). The new card in
     Settings honestly tells the user that, and provides the
     `adb shell pm disable-user --user 0 com.google.android.apps.tv.launcherx`
     recovery. The user must run this themselves; the launcher cannot.
  3. **Factory reset** clears role holders and disabled-flag state. After
     reset, both Google launcher and `com.google.android.tungsten.setupwraith`
     come back enabled. Re-run the adb recovery commands to restore.
- **Unit tests for the new helpers.** `LauncherScreenViewModelTest` exists,
  but `GoogleTvLauncherHelper`, `DestinationListSaver`, and the AppCard
  try/catch behavior have no direct coverage yet.

## Documents to read first

1. `AGENTS.md` — non-negotiable engineering rules (also guides AI agents).
2. `docs/PROJECT_CHARTER.md` — product principles including blind-first.
3. `docs/ROADMAP.md` — phases (Phase 5 "Performance and resilience" is the
   current one; Phase 6+ "Planned").
4. `docs/TALKBACK_TEST_PLAN.md` — the manual TalkBack checklist that gates
   the milestone exit.
5. `docs/DECISIONS.md` — durable decisions and the rationale for each
   non-obvious choice.
6. `docs/POLISH_LOG.md` — chronological polish history.

## Decisions worth re-reading

- **Settings page hides no real options but the card establishes honesty
  about the system-versus-app boundary.** The Google TV launcher card is a
  reconciliation of a long-standing mismatch between "the launcher would
  like to hand the Home button back to the user in-app" and "Android TV
  doesn't allow that with role + priority".

- **Generated `baseline-prof.txt` is committed.** The path
  `:baselineprofile` regenerates it; the committed copy is the canonical
  initial state for a fresh `assembleRelease` to consume without a device.

- **Destination backstack uses class-name Saver, not JSON.** Serializing the
  list of destination classes is more compact than a `java.io.Serializable`
  blob and aligns with the existing `@Serializable` annotation on each
  destination object.

- **Aggressive periodic refresh** is on a `RefreshStalenessTracker(60_000L)`
  debounce. A package change receiver handles live install/uninstall. Do not
  re-add "always refresh on RESUMED" without a different strategy —
  documentation explains why in `docs/CURRENT_MILESTONE.md`.

## Build / runtime knobs to be aware of

- `compileSdk = 37`, `targetSdk = 37`, `minSdk = 23`. Bump `compileSdk`
  separately; don't change `minSdk` without reviewing every
  package-visibility assumption — `androidx.tvprovider` 1.1 and the
  `androidx.tv.frameworkpackagestubs` permissions are sensitive.
- `aggressiveCordlessTabs`: none. Compose TV Material 1.1 is the version
  used. Bumping it changes focus-behavior contracts on cards; very visible
  to blind users.
- The `benchmark` build type is generated for the baseline profile; don't
  delete it.

## How to debug from the device

```
adb logcat -s LauncherFocus -v time              # focus and refresh timing
adb shell pm clear nl.ndat.tvlauncher           # clean state for repro
adb shell pm grant nl.ndat.tvlauncher \
  android.permission.READ_TV_LISTINGS          # allows channels to load
adb shell uiautomator dump /sdcard/s.xml        # hierarchy for a moment
adb shell am start -W nl.ndat.tvlauncher/.LauncherActivity  # cold start
```

For the Google TV launcher card's stuck-state scenarios, the adb recovery
is one line:
```
adb shell pm disable-user --user 0 com.google.android.apps.tv.launcherx
```
(re-enable Home routing on our launcher with:
`adb shell cmd role add-role-holder --user 0 android.app.role.HOME nl.ndat.tvlauncher`).

## Where to take it next (suggested order)

1. Push `fix/talkback-navigation` to your fork.
2. Open a PR upstream with the body you want (this file's
   **"What was just shipped"** section is the obvious base).
3. Do the manual TalkBack checklist in `docs/TALKBACK_TEST_PLAN.md` once,
   on the device with the help of someone who reads TalkBack aloud while
   you D-pad — capture any wording and focus issues for the next polish
   pass.
4. Once the checklist is green, move from Phase 5 to Phase 6
   ("Blind productivity features" in `docs/ROADMAP.md`). Resume-loop the
   AGENTS.md priorities again.

— end of handoff —
