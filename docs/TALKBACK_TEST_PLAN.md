# TalkBack and D-pad Test Plan

## Purpose

Validate the experience a blind person receives on a real Android TV. Automated build, lint, unit tests, UI hierarchy inspection, and debug focus timing are useful evidence, but none can prove spoken wording or practical remote navigation.

## Test setup

Record before testing:

- Date and launcher commit/build:
- TV/device model and Android version:
- TalkBack version and enabled state:
- Remote used:
- Starting launcher/default-launcher configuration:

Keep a working system launcher available during development. Do not change Home routing as part of this plan.

## Result record

| Flow | Steps | Actual focus and speech | Pass/Fail | Notes or issue link |
| --- | --- | --- | --- | --- |
| 2026-09-15 | Cold start (force-stop → launch) | Exactly one focused launcher node every step; focus restored to last toolbar item (Settings gear) after restart — restore path (LAST_FOCUSED_APP) works. Spoken wording not capturable via ADB. | Pass (focus) / Speech unverified | ADB-driven D-pad, TalkBack enabled |
| 2026-09-15 | Toolbar traverse Settings ⇄ tabs | LEFT/RIGHT walk Settings ↔ All apps ↔ Home, 13–29 ms per hop, edges stop cleanly, no blank/unlabeled stops. | Pass | Home tab shows `selected=true` while focused |
| 2026-09-15 | Tab activation | Re-activating the selected Home tab changed nothing (no Back-stack loop). Activating All apps logged `tab changed: Home -> Apps` exactly once; second activation was a no-op (content unchanged, no new log). | Pass | Speech not verified |
| 2026-09-15 | Settings round trip | Settings icon opens the Accessibility overview with entry focus accepted (`settings-entry: request accepted=true`); Back returned focus to the overview card; opening Focus restoration logged `settings-option: LAST_FOCUSED_APP request accepted=true`, Back returned to the overview focused. | Pass | Google TV launcher card present with merged name/state/action |
| 2026-09-15 | Rows traverse + row-end edge | D-pad DOWN from tabs reaches Favorites/Watch Next; program cards traverse right with 13–18 ms hops; at the Watch Next row end, RIGHT stops on the last labeled card (no blank stop, no trap). Row-end→Settings jump observed only on other rows; not retriggered on this run. | Pass | |
| 2026-09-15 | Popup open/Back round trip | Long-press on the first All apps card opened a single-action popup ("Add to favorites"; after favoriting the same card offered the remove path implicitly by second session state), popup first-action focus logged (`focus` + `popup: opened`), Back logged `popup-dismiss` and focus returned to the invoking card (same bounds). Favorite add then remove restored original state with focus preserved. | Pass | Speech wording of "single-announcement popup" still manual |
| 2026-09-15 | App launch from grid + return | Center on a grid app card launched the app (`com.example.playtorrio`); pressing HOME returned to `LauncherActivity` with focus restored to the same card; staleness-guarded refresh ran 486 ms (apps) without displacing focus. | Pass | HOME routed via device role setup (adb), not launcher code |
| 2026-09-15 | Grid focus flicker re-check | Not re-observed during this automated pass; requires physical-remote session to consider verified. | Pending manual physical-remote |
| — | TalkBack speech (wording, duplication, clones) | Not verifiable through ADB. | Pending manual | |

## Core checklist

### Startup and toolbar

- [ ] Launch the app. Confirm the initial focus matches the selected restoration mode and has a visible focus indicator.
- [ ] Traverse Clock, Home, All Apps, and Settings in every available D-pad direction. Confirm no blank stop, unlabeled control, or trap.
- [ ] Move focus across Home and All Apps without activation. Confirm displayed content and selected tab do not change.
- [ ] Activate each tab once and activate the selected tab again. Confirm one navigation action, correct selected state, and no Back-stack loop.

### Home, All Apps, and semantics

- [ ] Traverse Favorites, Watch Next, provider rows, and All Apps with the D-pad. Confirm every enabled control is reachable and visibly focused.
- [ ] Traverse the same controls with TalkBack. Each app must be one named actionable target, announced once.
- [ ] Confirm headings are useful for navigation and not repeated in each card announcement.
- [ ] Leave focus on a toolbar item while clock, application, and channel data update. Confirm focus and speech remain stable.

### Card menus and list mutations

- [ ] Long-press an app card. Confirm focus enters the popup, its pane/action names are meaningful, and disabled reorder actions cannot activate.
- [ ] Press Back to dismiss. Confirm focus returns to the invoking card and speech is concise.
- [ ] Add/remove first, middle, last, and sole favorites. Move a second item left and a penultimate item right. Confirm no stale popup, trapped focus, or duplicate speech.

### Settings and restoration

- [ ] Open Accessibility, then Focus restoration. Confirm page titles, headings, choices, descriptions, selected state, and entry focus are useful.
- [ ] Change a restoration mode, leave with Back, and reopen. Confirm it persists and the summary matches the selected mode.
- [ ] Test `LAST_FOCUSED_APP`: focus a favorite, leave/return or relaunch, and confirm that card restores when still available.
- [ ] Remove or unfavorite the stored app, then relaunch. Confirm the first available favorite is the safe fallback.
- [ ] Test other visible modes and record their actual, not assumed, behavior. Flag wording that overpromises.

### App and system return

- [ ] Launch an app from Home and return. Confirm the launcher is usable and focus restores predictably when the source card remains.
- [ ] Launch an app from All Apps and return. Record the restored target and spoken feedback.
- [ ] Open Settings from the toolbar and return. Confirm focus returns predictably to Settings.
- [ ] From a known All Apps card, press Back. Record the restored target and decide whether it matches the intended blind-user workflow.

## Responsiveness observation

Record an approximate perceived delay for D-pad focus and TalkBack speech after at least one tab movement, card movement, popup opening, and page change. Investigate only repeatable, user-noticeable delays; debug timing alone does not measure speech onset.

## Failure report template

```markdown
### YYYY-MM-DD — [short issue]

- Device / Android / TalkBack:
- Build or commit:
- Starting state:
- Exact remote or TalkBack steps:
- Expected focus and speech:
- Actual focus and speech:
- Reproducible: always | intermittent | once
- Accessibility impact:
- Supporting log or screenshot reference (without private data):
```
