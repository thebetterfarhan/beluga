# Accessibility regression checks

Baseline source: `8b7c84f`. Fix branch: `fix/talkback-navigation`.

The unchanged debug APK built successfully on 2026-09-09 with Java 21.0.12.1,
Gradle 9.7.1, Android platform 37.0, and the plugin-selected Build Tools 36.0.0.
The local baseline is saved at `build/baseline/tv-launcher-8b7c84f-debug.apk`.
Its SHA-256 is `040c7a1e77525c6bcf8a1c119bcb241fed0b3f52b86f336cca49d73483e07e19`.

## Device baseline

The unchanged launcher must be checked on an Android TV with TalkBack enabled.
No TV was connected during the initial setup, so spoken announcements and device
focus behavior have not yet been verified. Local tests do not replace this check.

Record the TV model, Android version, TalkBack version, launcher commit, and the
actual spoken output for each check. Run the same checks before and after fixes.
Keep the existing system launcher available during testing.

| Area | Steps | Expected after fixes |
| --- | --- | --- |
| Tabs | Move left/right between Home and All apps without pressing OK. | Focus moves; the displayed screen and selected tab stay unchanged. |
| Tab activation | Press OK on the focused tab; repeat on the selected tab. | The requested screen opens once; repeated activation creates no duplicate navigation entries. |
| Tab accessibility | Navigate to each tab with TalkBack and activate it. | Name, tab role, and selected state are available; accessibility activation opens the screen. |
| App tiles | Traverse Favorites and All apps with both the remote and TalkBack. | Each app has one named accessible control, with its name announced once. |
| Launch | Activate an app from both lists, then return to the launcher. | The app opens; focus returns predictably to the launching tile when it still exists. |
| App menus | Long-press a tile, traverse actions, then press Back. | Actions have meaningful names; Back dismisses the menu and returns focus to the tile. |
| Favorites | Add/remove a favorite and reorder first, middle, and last entries. | Actions announce their purpose; unavailable moves cannot activate; focus remains usable after list changes. |
| Toolbar | Traverse clock, tabs, Settings, and Inputs (when available). | No unlabeled action, unexpected screen switch, or focus trap. |
| Inputs | Open the input dialog, traverse entries, select an input; also dismiss with Back. | Input names are spoken; dismissal returns focus to Inputs. |
| Settings | Open system Settings and return with Back. | Settings opens and focus returns predictably to its toolbar button. |
| Back | From All apps press Back, then repeat on Home. | Returns to Home without duplicate Home entries or a navigation loop. |
| Layout updates | Focus a toolbar item while the clock updates or app/channel lists refresh. | Layout updates do not pull focus into the content area. |

## Local checks

Use Java 21 and an Android SDK with platform 37 installed:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest --console=plain --max-workers=1 '-Pkotlin.compiler.execution.strategy=in-process'
```

A test task reporting `NO-SOURCE` means there were no tests to run, not that
accessibility passed. Report compilation, lint, automated test results, and
manual TalkBack results separately.
