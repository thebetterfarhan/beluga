# Current Milestone: Performance and resilience

**Status:** In progress

## Objective

Keep interaction responsive on supported TV hardware and resilient to live data changes. Measure real responsiveness on the Chromecast target device before optimizing; minimize main-thread work in focus and refresh paths.

## Scope

- Stale-refresh guard so `LauncherActivity` only re-queries apps/channels when data is genuinely stale (60 s window). Live app changes remain covered by the registered `PackageChangeReceiver`.
- Batched preview-channel program commits into a single transaction to reduce DB-notification emissions and recomposition churn.
- Measured Back-transition main-thread jank (`Davey!` frames ~1–2 s).
- Main-thread work minimization in focus and refresh paths.

## Out of scope

- Intercepting or changing system Home-button routing/default-launcher behavior.
- Broad visual redesign, new features, or accessibility semantics changes (belongs to the accessibility foundation milestone).
- Full launcher-state restoration beyond the currently documented fallback behavior.
- Hand-wiring focus graphs (the `focusProperties` custom-graph approach was rejected and is documented as a decision).

## Completed and evidenced

- [x] Stale-refresh guard implemented in `LauncherActivity`: `REFRESH_STALE_MS = 60_000L`; live app changes still handled by `PackageChangeReceiver`.
- [x] Preview-channel program commits batched into one transaction in `ChannelRepository`.
- [x] Device evidence on Chromecast (`sabrina`, Android 14): cold-start apps refresh measured at **3,354 ms**; return after >60 s measured at **1,470 ms**; return within 60 s correctly skips the refresh (`appsStale=false`/`channelsStale=false`).
- [x] `LauncherFocus` instrumentation added (`resumed: refresh staleness check`, per-refresh timing, staleness flags, failure messages) — debug-only, `BuildConfig.DEBUG`-gated.
- [x] One Back-transition jank contributor removed: debug placement counters in the Accessibility overview and Focus restoration screens wrote Compose state from `onPlaced`, creating a layout/recomposition feedback loop. On Chromecast the loop logged tens of thousands of placements before 1.3 s and 2.1 s `Davey!` frames. Both remaining non-state placement counters and their per-placement logs were also removed on 2026-09-14; the screens retain their snapshot-guarded entry-focus requests and one-time acceptance logs.
- [x] Unchanged app/channel/program refreshes now skip database writes. This avoids SQLDelight flow invalidations, recomposition, focus movement, and repeated screen-reader output when the queried content is identical.
- [x] App-card artwork resolves off the main thread, and null artwork no longer creates failed Coil requests while a card is becoming visible.
- [x] Settled Chromecast stale refresh after those changes: apps **295 ms**, channels **718 ms**, no `LauncherFocus` movement, and a worst sampled frame of **200 ms**. A within-60-second return correctly skipped both refreshes.
- [x] `:app:assembleDebug`, `:app:lintDebug`, `:app:testDebugUnitTest` green.
- [x] Failed and lifecycle-cancelled refreshes no longer advance their staleness timestamps. The next `RESUMED` retries instead of suppressing fresh data for 60 seconds; coroutine cancellation is rethrown rather than logged as an ordinary failure.
- [x] `RefreshStalenessTrackerTest` covers the stale-until-success and exact 60-second boundary behavior. The tracker keeps refresh-failure/cancellation retry logic platform-independent and testable.
- [x] Automated hierarchy check on `sabrina` with the launcher visibly foregrounded found exactly one focused launcher node at each step: Home -> All apps -> Settings -> All apps -> Home -> Just Player -> Home. The observed labels and D-pad restoration were correct; this does not prove TalkBack speech.
- [x] Automated card-menu hierarchy check: a long D-pad-center press on `Just Player` focused the labeled `Remove from favorites` action, and Back restored focus to `Just Player`. Popup speech and all mutation paths still require manual TalkBack validation.
- [x] Focus-restoration detail hierarchy check found exactly one focused selected `Home tab` radio row, exposing its name, description, checked state, and RadioButton role. The surrounding `Settings` label is pane context, not the focused control.
- [x] A 20-second physical-remote Back capture completed on `sabrina`. The Accessibility overview entry requester was accepted on each observed return; the run is not a fair responsiveness baseline because swap was nearly full and both the launcher and TalkBack were active CPU consumers.
- [x] Controlled physical-remote check: from Accessibility, opening Orientation help and pressing Back exactly once returned focus to the Accessibility overview. The prior toolbar-focus log was therefore an uncontrolled-sequence observation, not a confirmed return-focus regression.
- [x] The retained 20-second `atrace` was decoded and analyzed locally. Launcher main-thread runnable wait was negligible (318 samples, maximum 1.527 ms, 95th percentile 0.110 ms), while continuous main-thread CPU slices reached 207.951 ms (631 samples, 95th percentile 27.117 ms). The longest slices correlated with `Choreographer#doFrame`, `traversal`, `draw`, `Record View#draw()`, and animation, proving the captured jank is launcher-owned draw/animation CPU work rather than scheduler starvation.
- [x] Watch Next poster requests no longer force software bitmaps. `ChannelProgramCard` now lets Coil use hardware-backed images where supported, avoiding unnecessary software-bitmap preparation and texture uploads observed in the trace. The guarded `sabrina` build/install passed; Home exposed Watch Next, and a four-step D-pad traversal retained exactly one focused node with complete labels for each app/program card.
- [x] App and Watch Next cards no longer enlarge on focus; their existing 2 dp high-contrast focused border remains. Two identical 32-key Home/Watch Next workloads on `sabrina` showed consistent percentile improvement versus the default-scale baseline: baseline 50th/90th/95th/99th was 950/1550/2700/4250 ms; retained no-scale runs were 750/1550/2500/4150 ms and 600/1400/2500/3900 ms. Focus latency remained comparable (15–68 ms), focus events remained complete, and no crash occurred. All frames were still classified janky, so this is a bounded improvement rather than milestone completion.
- [x] Captured and locally analyzed a fresh 32 MB-buffer compressed `atrace` from the retained no-scale/hardware-bitmap build during the 32-key Home/Watch Next workload. Launcher main-thread CPU slices reached 324.324 ms (4,224 samples; 95th percentile 32.148 ms), while runnable waits remained negligible (928 samples; maximum 4.505 ms, 95th percentile 0.117 ms). Long sections include `AndroidOwner:measureAndLayout` (max 1,535.75 ms), `Recomposer:animation` (615.30 ms), `Recomposer:recompose` (488.75 ms), lazy prefetch (323.56 ms), semantics-node collection (239.10 ms), and input/focus dispatch (216.64 ms).
- [x] Program-row focus state no longer invalidates every visible program card. Each card now remembers a stable `FocusState` callback that writes through a stable focused-program state object; only the details panel observes the focused value. In matched card-labeled traces, program-card composition dropped from 41 sections / 1,424.327 ms to 36 / 1,191.900 ms; Coil painter setup dropped from 53 / 491.904 ms to 44 / 402.650 ms; total `Compose:recompose` dropped from 4,639.950 ms to 4,071.550 ms. The final clean APK passed build, lint, tests, install, and startup hierarchy validation on `sabrina`.
- [x] Focused-program detail updates now read state in their own restartable composable instead of the `ChannelProgramCardRow` scope. This keeps title/description updates from restarting the parent scope that owns the lazy cards. The unused `App` argument was removed from the row/details path. A matching clean ADB workload remained crash-free and improved directionally from 750/2000/2600/3900 ms to 600/1500/1600/1900 ms at the 50th/90th/95th/99th percentiles; GPU percentiles stayed 12/14/14/15 ms. Treat the frame comparison as supporting evidence because device pressure and ADB-driven `gfxinfo` remain noisy.
- [x] Back from the All-apps grid to Home no longer lands on the global Settings icon: the launcher toolbar no longer wraps its row in `focusRestorer()`. Tab-row focus restoration is now scoped to `ToolbarTabs` only. Verified on `sabrina` that Apps → BACK lands on the All-apps tab, and a second Apps/Back cycle lands on the same tab consistently.
- [x] Focus-restore accessibility dialog no longer repeats option descriptions. Each card exposes `contentDescription = name`, `stateDescription = description`, `selected`, and `Role.RadioButton`; visible inner text nodes are silenced for TalkBack via `clearAndSetSemantics`. The page-title text no longer competes with the pane title, the LazyColumn carries a `focusRestorer()` for Back-into-page consistency. Verified on `sabrina`: hierarchy dump shows one `content-desc` per card matching its name only, entry focus lands on the currently selected mode.
- [x] Channel program details panel uses `clearAndSetSemantics` on its title and description texts so the same content doesn't fire as a second TalkBack announcement when the row above is focused.
- [x] Home AppPopup routes its entry focus consistently to the favorite IconButton; the move-left / move-right buttons no longer carry the entry-focus modifier. Verified the popup opens with the favorite button focused.
- [x] Orientation help screen title no longer carries both `heading()` and `paneTitle`; TalkBack announces it once on entry instead of twice.

- [x] Baseline-profile architecture was added and validated: a `:baselineprofile` generator module plus the `androidx.baselineprofile` plugin produce a committed startup profile for the release build (`app/src/main/generated/baselineProfiles/baseline-prof.txt`). On `sabrina` (Android 14), collection completed and was re-verified across two sessions with five stable iterations each (`speed-profile` dexopt performed; startup measured inside the profiled scope: 568 ms), the rebuilt APK embeds `assets/dexopt/baseline.prof`, `assembleNonMinifiedRelease`/`lintDebug`/`testDebugUnitTest` stayed green, ProfileInstaller reported "Installing profile" after install, and repeat force-stopped cold starts measured `am start -W` TotalTime 1,069/1,199/1,075 ms and 1,087/1,268/934 ms. A same-build profile-less release baseline proved unconstructible through the plugin (dependency AAR profiles are auto-merged, and the plugin's captured output persists into merges), so the profile gain over a truly profile-less build is not quantified. Known friction: the `generateBaselineProfile` connected task still exits nonzero after a fully successful on-device run (JUnit XML shows zero failures; AGP decision path is `test-result-exit-code.txt = 1`); until the toolchain resolves it, copy the produced `baseline-prof.txt` into `app/src/main/generated/baselineProfiles/` by hand (recorded in `docs/DECISIONS.md` and `POLISH_LOG.md`).

## Open validation

- [x] Re-measured the Back transition with the physical Chromecast remote after removing the placement-counter feedback loop. The tester reported both documented flows passed; ADB cannot reliably replay this TalkBack-routed navigation path.
- [x] A bounded 10-cycle Android Settings → launcher background/foreground soak on `sabrina` retained exactly one focused launcher node labeled `Home` on every return. `LauncherActivity` remained top-resumed, and filtered `LauncherFocus`/`AndroidRuntime` logs contained no refresh failure, cancellation entry, or crash. This supplies hierarchy-level evidence that lifecycle cancellation does not destabilize return focus; spoken output still requires manual TalkBack validation.
- [ ] Re-check the one-off Apps-grid focus flicker during a physical-remote session (previously attributed to RESUMED refresh re-emission; the stale guard should reduce its likelihood).
- [ ] Decide the preferred blind-user Back outcome from a focused All Apps grid card. The 2026-09-14 hierarchy check returned focus to Settings after one Back with no focus loss; TalkBack wording and user workflow preference remain unverified.
- [ ] **Manual TalkBack + physical-remote pass** — the accessibility foundation milestone's remaining requirement (speech output cannot be captured via ADB). Includes the flagged judgement calls: grid flicker re-check, last-favorite→Settings row-end jump, single-announcement popup wording.

## Current blockers and risks

- The latest 20-second capture is unsuitable for quantitative performance conclusions: 31 of 38 frames were janky (90th percentile 950 ms), while swap was 479 MB of 496 MB used and the launcher/TalkBack were active CPU consumers. Its focus log is useful evidence, but its timing is not a clean launcher baseline.
- A follow-up 20-second capture again showed main-thread jank (13 of 14 frames; 90th percentile 600 ms; one 1,000 ms `Davey!` frame) with GPU 90th percentile only 14 ms and swap still 478 MB of 496 MB used. The pattern merits a clean-device baseline before a launcher rendering change or a baseline-profile architecture addition.
- After a Chromecast reboot, a further 20-second capture still had 18 janky frames out of 18 (90th percentile 1,050 ms; 95th percentile 1,700 ms) with GPU 90th percentile 13 ms. Reboot reduced used swap but did not remove it (390 MB of 496 MB used at capture start), so this confirms the issue is reproducible but not yet attributable solely to launcher code.
- Local analysis of the bounded system trace isolated launcher-owned long CPU slices in Compose/View draw and animation work; it did not expose composable-level names for the largest `Record View#draw()` spans. Re-capture the same physical Back flows before attributing a quantitative improvement to the hardware-bitmap change.
- The controlled Home/Watch Next workload still reports 100% janky frames even after removing card enlargement, despite responsive focus callbacks and 6–15 ms GPU work. Remaining cost is CPU-side and needs a more focused current-build trace; do not infer user-perceived multi-second focus delay from `gfxinfo` alone.
- The current-build trace confirms broad Compose measurement/recomposition, TalkBack semantics processing, and lazy prefetch overlap focus movement. A controlled removal of the focused-title marquee reduced median frame time but worsened 90th-percentile and focus-event latency (worst callbacks 399–434 ms versus 55–68 ms), so it was reverted. Do not remove the marquee again without a different implementation and stronger evidence.
- A UI hierarchy cannot prove spoken output or speech timing; manual TalkBack validation is required.
- The 2026-09-14 post-install UI hierarchy belonged to the system launcher because a Dream/default-launcher overlay owned the visible window, even though `LauncherActivity` was top-resumed. It is not evidence for this launcher's initial focus.
- Install the TV build only on the Chromecast currently connected via wireless ADB (`sabrina`, Android 14); do not deploy it to any other device.
- `COMPLETE_LAUNCHER_STATE` is not implemented as a full state restore. Its visible wording must remain honest until it is implemented or removed.
- The placement-loop contributor was fixed in code, and the tester reported the post-fix physical-remote Back flows passed. A frame-timing capture on an otherwise idle device is still needed before making a quantitative responsiveness claim.
- The first post-fix physical-remote capture is not a fair launcher baseline: the Chromecast had about 166 MB free RAM, nearly full swap, and OTT Navigator consuming a full CPU core. App-card asset reads measured 4–5 ms, so they are not the observed multi-second source. Re-run only with the Chromecast otherwise idle; do not stop the user's apps or change device state without permission.
- Cold process startup remains slow on this debug build even with an otherwise CPU-idle Chromecast (swap remains nearly full). A test-only `speed` compilation improved the cold-frame median but did not eliminate multi-second frames. A production baseline-profile setup is a reasonable next architecture change, but requires an explicit decision because it adds build/release infrastructure.
- No change should be made to Home routing without an explicit, separately scoped decision.
- `LauncherActivity` now gates refreshes by staleness; if a package-change fires while the process is backgrounded and the receiver's work is incomplete, the next `RESUMED` within the stale window may skip a needed refresh. The 60 s window is intentionally generous, and `PackageChangeReceiver` covers live installs.

## Exit criteria

- [x] Post-fix Back-transition behavior was validated with the physical Chromecast remote; quantitative timing remains pending an idle-device evidence capture.
- [ ] Refreshes do not steal focus or cause repeated speech.
- [ ] Main-thread work in focus paths is minimized (stale guard verified; Back-transition jank scoped).
- [ ] Manual TalkBack and physical-remote checks affected by changed focus or refresh behavior are completed or explicitly recorded as pending.
- [ ] Results and unresolved findings are recorded in `docs/POLISH_LOG.md`.

## Quantitative capture follow-up

With the Chromecast otherwise idle, run `util\CollectChromecastBackEvidence.cmd "C:\path\to\adb.exe"` while using the physical remote: enter Accessibility → Focus restoration, press Back, then enter Orientation help and press Back. Review its device-pressure, focus, and frame sections; if another app is consuming CPU or memory, discard that run rather than attributing it to the launcher. Confirm the overview remains focused with no `Davey!`/skipped-frame event. Record the observed delay and TalkBack wording. Manual TalkBack spoken validation remains the accessibility foundation milestone's last requirement.

## Next concrete action

Complete the remaining manual TalkBack and physical-remote checklist paths: Apps-grid flicker, the last-favorite row-end jump to Settings, popup single-announcement wording, app-card options, app return, and Watch Next. Treat the optional quantitative Back capture separately; it must be run only when the Chromecast is otherwise idle. The profile-less cold-start comparison is recorded as toolchain-limited: a quantitative own-profile gain would require a dedicated no-profile build variant or plugin disable, and is not worth new build infrastructure now.

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
