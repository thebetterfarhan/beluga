# Decisions

Record durable choices here. Entries state what was chosen, why, consequences, and what would justify revisiting it.

## Accessibility settings use dedicated pages

**Status:** Accepted

**Decision:** Accessibility settings, including focus restoration, use dedicated Compose pages rather than a transient dialog.

**Reason:** Pages give D-pad and TalkBack users a stable title, predictable entry focus, clear Back behavior, and room to grow.

**Consequences:** Navigation and focus restoration must be tested when entering and leaving the settings pages.

## Preserve TV-card focus while removing duplicate card speech

**Status:** Accepted

**Decision:** Keep the Compose TV Material card as the actionable focus target. Give the card the app label and keep its image and title descendants silent where needed.

**Reason:** Replacing the card semantics would risk breaking established TV focus behavior. Silent decorative descendants prevent the app name from being announced more than once.

**Consequences:** Every card change must be checked for one actionable node and one useful spoken name.

## Default focus-restoration behavior is last focused favorite

**Status:** Accepted, with fallback

**Decision:** The default mode is `LAST_FOCUSED_APP`. Store the last focused favorite privately and restore it when it still exists; otherwise focus the first available favorite.

**Reason:** Returning people to their prior app most directly preserves orientation.

**Consequences:** Persistence must not block the UI thread. Missing, unfavorited, or removed apps require a safe fallback.

## Full launcher-state restoration is deferred

**Status:** Deferred

**Decision:** Do not claim full restoration until tab, focused item, scroll position, and expanded state can actually be persisted and restored.

**Reason:** A misleading setting harms trust, especially for screen-reader users.

**Consequences:** Current UI text must state the real fallback behavior; implementation belongs in a later scoped task.

**Implementation note (2026-09-15):** Tab-level restoration (Home/Apps) now ships behind `COMPLETE_LAUNCHER_STATE` via `LauncherStateStore`; focused item, scroll position, and expanded state are still not restored, and the settings description states this. The mode remains partially implemented, so the "do not claim full restoration" rule still applies.

## Focus moves independently from tab activation

**Status:** Accepted

**Decision:** D-pad traversal over toolbar tabs changes focus only. Activation changes destination once and must not create duplicate Home history entries.

**Reason:** This is predictable remote behavior and avoids accidental context changes during exploration.

**Consequences:** Test focused versus selected tab state, repeated activation, and Back navigation.

## Home routing is not part of this milestone

**Status:** Accepted for current milestone

**Decision:** Do not change Android system Home-button routing or default-launcher behavior while completing accessibility settings and focus validation.

**Reason:** It is platform- and user-configuration-sensitive work outside the focused milestone.

**Consequences:** Treat any Home-routing issue as separate work requiring explicit scope and device testing.

## Radio lists use default lazy-container focus search

**Status:** Accepted

**Decision:** Settings radio lists use the LazyContainer's default focus search with `focusRestorer()`, plus a placement-based entry focus request guarded by a snapshot flag. Do not hand-wire a `focusProperties { up/down }` focus graph on top of TV Material interactive cards, and do not use a one-shot `LaunchedEffect { delay(...); requestFocus() }` for entry focus.

**Reason:** `androidx.tv` Material `Card` applies its own internal `focusable()` through `tvClickable` (verified in the tv-material 1.1.0 AAR), and a custom focus graph on such cards produced a D-pad focus trap on the Chromecast. Default lazy focus search matches the proven Apps grid and Home rows. A placement-based entry request re-applies on every placement, so it survives the janky recomposition seen during Back transitions, while a delayed one-shot request cannot recover once a late recomposition re-runs default focus search.

**Consequences:** Entry focus must be requested from `onPlaced` (snapshot-guarded) and page return focus uses the same mechanism plus `focusRestorer()` on the page's lazy container. Back transitions may still jank on this device, but the focus outcome remains correct.

**Revisit when:** Switching to a non-lazy layout that lacks native directional focus search, or if a future tv-material version changes Card focusability.

## Focused cards use a border without enlargement

**Status:** Accepted

**Decision:** App and Watch Next cards keep their existing 2 dp focused border but use `CardDefaults.scale(focusedScale = 1f)` so focus does not enlarge the card.

**Reason:** The retained trace isolated long launcher-owned CPU slices inside frame animation and display-list recording. In two repeated 32-key device workloads, disabling focused enlargement consistently improved frame percentiles without changing focus-event completeness or D-pad latency. The border remains an explicit, high-contrast visual focus indicator, so enlargement is redundant rather than the sole focus cue.

**Consequences:** Focus movement performs less transform/recording work and avoids layout overlap from enlarged TV cards. D-pad focus, semantics, card actions, popup behavior, and TalkBack labels are unchanged. The device still reports substantial CPU-side jank, so this decision does not close the performance milestone.

**Revisit when:** A future TV Material release materially reduces focus-scale cost, user testing finds the border alone insufficient, or a reduced-motion/accessibility preference provides a better user-controlled policy.

## Decision template

```markdown
## [Decision title]

**Status:** Proposed | Accepted | Superseded | Deferred

**Decision:**

**Reason:**

**Consequences:**

**Revisit when:**
```

## Refresh on RESUMED uses a stale guard instead of always querying

**Status:** Accepted

**Decision:** `LauncherActivity` refreshes apps and channels on `RESUMED` only when the cached data is older than `REFRESH_STALE_MS = 60_000L`. Live app changes are already covered by the registered `PackageChangeReceiver`, so per-resume re-queries are unnecessary. The staleness timestamps reset on every successful refresh. Debug instrumentation (`debugLauncherLog`) records `appsStale`/`channelsStale` and per-refresh timing.

**Reason:** The previous every-`RESUMED` refresh ran a full package-manager query and SQLDelight commit every time the launcher returned from another app (including seconds after launch). On Chromecast this takes ~3.3 s for apps alone, triggers DB-flow re-emissions, recomposition, and focus churn â€” contradicting the charter goals "Refreshes do not steal focus or cause repeated speech" and "Main-thread work in focus paths is minimized." The `PackageChangeReceiver` already keeps the app list current when packages change while the process is alive.

**Consequences:** App return within the 60 s window skips the refresh entirely. Data may be up to 60 s stale on return â€” acceptable given the receiver coverage. `COMPLETE_LAUNCHER_STATE` and other modes are unaffected. A lifecycle dip may cancel the `repeatOnLifecycle` child normally; cancellation propagates without being misreported as a refresh failure, and incomplete work remains stale for the next resume.

**Implementation note (2026-09-14):** Staleness timestamps are written only after their individual refresh succeeds, so an error or lifecycle cancellation is eligible for retry on the next resume. `CancellationException` is rethrown to preserve structured-concurrency semantics instead of being logged as an ordinary failure.

**Verification note (2026-09-14):** `RefreshStalenessTrackerTest` covers stale-until-success behavior and the exact 60-second boundary without requiring an Android runtime.

**Revisit when:** A package-change event is reliably missed (e.g., receiver dropped while the process was backgrounded), or the staleness window proves too long for a specific use case.

## Radio lists use default lazy-container focus search

## Cold start uses a generated Baseline Profile

**Status:** Accepted (2026-09-15)

**Decision:** Ship a generated Baseline Profile with the release build. A dedicated `:baselineprofile` (`com.android.test`) module holds `BaselineProfileGenerator`; the app applies the `androidx.baselineprofile` plugin, depends on `androidx.profileinstaller:profileinstaller`, and keeps the committed profile at `app/src/main/generated/baselineProfiles/baseline-prof.txt` (`saveInSrc = true`, `mergeIntoMain = true`). Generation runs against the connected Chromecast via `:app:generateBaselineProfile` using the `nonMinifiedRelease` variant.

**Reason:** Cold process startup remained slow on the Chromecast even when otherwise idle; the milestone identified a production baseline profile as a reasonable architecture change. The profile pins launcher startup classes (application/activity bootstrap, Koin, Compose, toolbar and Home tab focus composition) into ART's dex layout and JIT state, which is the documented startup improvement without behavior changes.

**Consequences:** Adds the `:baselineprofile` module and three new androidx versions (baselineprofile `1.5.0-alpha02` for AGP 9 compatibility, benchmark `1.5.0`, profileinstaller `1.4.1`), plus a `benchmark` app build type used only for generation. The profile must be regenerated when startup-critical code changes (new application bootstrap, navigation, focus setup); re-run `:app:generateBaselineProfile` with the Chromecast connected. The connected run currently exits nonzero after a fully successful on-device collection (AGP/benchmark exit-code friction, first observed 2026-09-15); the collected profile at `baselineprofile/build/outputs/connected_android_test_additional_output/.../baseline-prof.txt` must then be copied to `app/src/main/generated/baselineProfiles/baseline-prof.txt` manually until the toolchain resolves it. User-visible behavior, focus, and accessibility semantics are unchanged.

**Verification note (2026-09-15):** The generated profile (1.5 MB text) was produced on `sabrina` (Chromecast, Android 14) across five successful collection iterations. The rebuilt APK embeds `assets/dexopt/baseline.prof` and `profileinstaller`; `assembleNonMinifiedRelease`, `lintDebug`, and `testDebugUnitTest` are green. After install, ProfileInstaller reported "Installing profile" and three force-stopped cold starts measured `am start -W` TotalTime 1,069/1,199/1,075 ms. No same-build profile-less release baseline exists yet, so treat the improvement as directional rather than quantitative.

**Revisit when:** The generation task exits cleanly on `sabrina`, a profile-less release baseline is measured, or startup code shapes change materially.

## Disabling the stock Google TV launcher is delegated to the system App-Info page

**Status:** Accepted (2026-09-15)

**Decision:** The Settings overview exposes a "Google TV launcher" card that displays the stock launcher's enabled/disabled state and opens the system App-Info page (`ACTION_APPLICATION_DETAILS_SETTINGS`) on activation. The launcher itself never calls package-management APIs to enable/disable another app.

**Reason:** A regular app cannot change another package's enabled state without device-owner or system privileges, and provisioned devices (this Chromecast) cannot be granted device-owner post-setup. The system App-Info page exposes Disable/Enable with the system's own guard rails, and it is D-pad reachable. State detection via `PackageManager.getApplicationEnabledSetting` is read-only and refreshed on app RESUMED through a lifecycle observer, so spoken state stays accurate without polling or focus churn.

**Consequences:** The toggle flow leaves the launcher (system page) and relies on Back restoration, verified via hierarchy checking with focus returned to the same card. Users manage the toggle with the system's confirmation dialogs. Factory reset reverts all device-side Home routing (both disabled packages return and Google's launcher becomes HOME again); the documented recovery is the adb command set recorded in the session notes. Spoken wording still requires manual TalkBack validation.

**Revisit when:** The launcher could realistically become device-owner (e.g., for a managed deployment), or a future Android release exposes home-app enable/disable to regular apps.

## Google TV launcher card reports three states and depends on adb for re-disable

**Status:** Accepted (2026-09-15)

**Decision:** The Settings "Google TV launcher" card shows one of three states: Disabled, Enabled, or "Enabled and it owns the Home key" (via `resolveActivity(MAIN+CATEGORY_HOME, MATCH_DEFAULT_ONLY)`). Disabled/Enabled opens the system App-Info page. When the stock launcher owns Home resolution, activation opens the system Default-home chooser and the description states explicitly that the system settings page cannot re-disable the stock launcher, giving the adb recovery command instead.

**Reason:** Device testing on the Chromecast proved the stock launcher's home intent filter has `priority=2`; any enabled instance defeats `pm set-home-activity`, the role holder, the Default-home chooser, and role-controller records at default resolution. Android also strips non-system manifest intent-filter priorities, and the TV App-Info page never offers a Disable action for the stock launcher. Device-owner (which could hide packages) is unobtainable on provisioned devices with existing accounts. Therefore the launcher cannot toggle the Google launcher both ways in-app; stating it honestly and giving the working adb command is the best blind-first behavior.

**Consequences:** Card activation differs by state (App info for Enable, Default-home chooser for the stuck state). Spoken labels are verified in the hierarchy; single announcement each time. Device recovery from enabling the stock launcher remains adb-only; the docs also record the rapid reset path (factory reset reverts everything).

**Revisit when:** Android TV exposes home-override to regular apps, a system update reduces the stock launcher's priority, or device-owner can legitimately be granted on the target device.

## Back-from-Apps focuses on a Home-tab target, not a global toolbar-last-focused one

**Status:** Accepted (2026-09-15)

**Decision:** The toolbar row no longer applies `focusRestorer()`. Tab-row focus restoration lives only inside `ToolbarTabs`.

**Reason:** The previous toolbar-level `focusRestorer()` used the most-recent focused descendant of the entire row — tabs and the Settings icon together — so Back from the Apps grid to Home restored focus to whatever was last touched globally, typically the Settings gear (the most-natural-to-touch besides the tabs). Removing that scope and keeping restoration scoped to the tabs row makes Back return to the All apps tab the user just left, matching their intent.

**Consequences:** Cold-launch focus still lands on the Settings button (default tab traversal); Settings remains reachable via DPAD_RIGHT from any tab. The earlier placement-counter feedback-loop fix is unaffected (it was about the accessibility-overview screen, not the launcher toolbar). All apps / Back round trips now use the All-apps tab as the consistent restoration target.

**Revisit when:** A more sophisticated destination-level focus state (focused card on Home, scroll offset, expanded row, etc.) is introduced — at that point a per-destination focus recorder becomes worth designing deliberately rather than leaning on the built-in `focusRestorer()`.
