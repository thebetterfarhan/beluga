# Architecture

## Current structure

`app` is the only Android module. It is a Kotlin/Jetpack Compose launcher with package `nl.ndat.tvlauncher`; `LauncherActivity` hosts the Compose UI. The manifest exposes the activity as `HOME`, Leanback, and standard launcher activity, and declares TV listings plus package-change/boot receivers.

`ui/screen/launcher/LauncherScreen.kt` creates the top-level column: `Toolbar` followed by a navigation display. `ui/toolbar/Toolbar.kt` contains Home/All apps tabs, optional input switching, system Settings, and a clock. `data/Destinations.kt` and the Navigation 3 back stack select the Home or Apps tab content.

Home (`ui/tab/home`) is a vertical list of horizontal card rows: favorites, Watch Next, and provider channels. All apps (`ui/tab/apps`) is an adaptive lazy grid. Both render reusable cards from `ui/component/card`; app cards launch the chosen Leanback/default intent and may expose a long-press action menu through `PopupContainer`.

The data layer combines TV-provider, package, and input resolvers with repositories and SQLDelight persistence (`data`, `sqldelight`). Koin supplies view models and repositories. Package changes refresh the app data.

## Interaction boundaries

- Focus containers use Compose TV Material components and `focusRestorer`; navigation containers use stable item keys.
- `LauncherScreen` applies `autoFocus` to the content host, so focus behavior must be checked when destinations change or UI data refreshes.
- `PopupContainer` creates a focusable Compose `Popup`; dialog/menu opening and dismissal must restore focus to the invoking card.
- Card image content is labelled with the app name. The enclosing actionable card must remain a single, non-duplicated screen-reader target.

See `accessibility/ACCESSIBILITY_REQUIREMENTS.md` for required behavior and `accessibility/TEST_CHECKLIST.md` for regression steps.
