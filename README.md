# Beluga

**The Android TV launcher built for everyone.**

Windows has Jaws. Linux has Orca. Now Android TV has Beluga.

Beluga is an accessibility-first launcher for Chromecast with Google TV, Nvidia Shield, and any Android TV device. It replaces the default launcher with a D-pad-native interface where TalkBack isn't an afterthought — it's the foundation.

No setup required. No complex menus. Just plug it in, set it as your launcher, and go.

<img src="https://user-images.githubusercontent.com/2305178/186512479-e94bf85d-ac09-4f9d-b54e-24bcf43c82da.png" height="400" />

## Why Beluga?

Most TV launchers are built for everyone — then accessibility is bolted on later, if at all. Beluga was built the other way around: a fully navigable, announcable, D-pad-native interface where screen reader support isn't a feature. It's just how it works.

- **TalkBack just works** — every card, row, and tab is announced clearly
- **Focus stays where you left it** — Beluga remembers your place, not just the screen you were on
- **No hunting through menus** — every important setting is two clicks away
- **Runs on what you already own** — Chromecast with Google TV, Shield, anything Android TV 6.0+

## Features

### Home Screen

| Row | What it does |
|-----|-------------|
| **Continue** | Jumps straight back into the last app you had open |
| **Recent** | Your last 4 opened apps, most recent first |
| **Favorites** | Apps you bookmarked — long-press any app card to add or remove |
| **Watch Next** | TV program recommendations from the system |
| **Channels** | Live channel rows from your TV provider |

### All Apps

A full grid of every launchable app on your device. Search by name or package. Long-press any card to favorite it, hide it, or move it.

### Accessibility Settings

Everything in one place — no digging through Android settings:

- **Focus Restoration** — choose where focus goes when you return: last app, first favorite, Home, or All Apps
- **Orientation Help** — a built-in guide to D-pad navigation and long-press menus, available anytime
- **System Accessibility** — direct shortcuts to TalkBack, audio descriptions, and high-contrast text settings
- **Hidden Apps** — hide apps from the grid without uninstalling them
- **Home Layout** — toggle the Continue, Recent, and Watch Next rows on or off
- **Recently Updated Apps** — see what's been updated and jump straight to the Play Store
- **Channel Order** — drag and drop to reorder TV channel rows

## The Small Stuff

Beluga sweats the details other launchers ignore:

- **Explicit LazyColumn keys** on every settings item — no flicker when the screen recomposes
- **Caching everywhere** — SharedPreferences reads are cached, database queries don't fire redundantly
- **Cursor cleanup guaranteed** — no leaked cursors when TV provider data is malformed
- **Clean logs in release** — debug logging is off by default, not sprinkled across the codebase

## Platform Limitations

Some things Android just won't let you do — Beluga is honest about them:

| What | Why |
|------|-----|
| **Wallpapers** | `WallpaperManager` has no implementation on Android TV — this is an Android platform limitation, not a Beluga limitation |
| **Power controls** | `goToSleep()`, `shutdown()`, and `reboot()` require a signature permission no third-party app can get |
| **Accessibility announcements** | The `AccessibilityManager.announceForAccessibility()` API is unreliable in the AndroidX Core version used on API 33+ — Beluga uses `clearAndSetSemantics` + pane announcements instead |

## Supported Devices

- Chromecast with Google TV (all generations)
- Nvidia Shield (2015 / 2017 / Pro / Android TV)
- Any Android TV device running Android 6.0+ (API 23)

Not sure if yours is supported? Try installing it. The worst case is it doesn't show up in your launcher list.

## Building

Java 21 and Android SDK 37 required.

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

For release builds, add a `keystore.properties` at the project root:

```properties
keystorePath=/path/to/keystore.jks
keystorePassword=yourpassword
keyAlias=beluga
keyPassword=yourpassword
```

## Changelog

### [Unreleased] — Beluga

#### Features
- **Focus Restoration** — configurable startup focus: last focused app, first favorite, Home tab, All Apps tab, or full state restoration
- **Onboarding Card** — first-launch TalkBack guide to D-pad controls, shown once and dismissable
- **Accessibility Overview** — centralized hub with system accessibility status, audio descriptions, high-contrast text, and a recently updated apps card
- **Recent Updates Tracker** — `PendingUpdatesStore` records `PACKAGE_REPLACED` broadcasts with a 24-hour window; count announced on All Apps tab entry
- **Package Change Receiver** — refreshes the app list on `PACKAGE_ADDED`, `PACKAGE_CHANGED`, `PACKAGE_REPLACED`, `PACKAGE_REMOVED`, and `BOOT_COMPLETED`

#### Bug Fixes
- **Cursor leak (ChannelResolver)** — all three cursor-iterating methods now use `cursor.use {}` to guarantee cleanup on any exception
- **Silent exception swallowing (PackageChangeReceiver)** — empty `catch` replaced with `Timber.e()` logging
- **Race: HiddenAppsViewModel** — `_hiddenApps` is now a pure `combine().stateIn()` derived flow; the DB collector can no longer overwrite a user-initiated unhide
- **Race: ChannelPreferencesViewModel** — channel list is now a pure derived flow; manual reorders update SharedPreferences without mutating the displayed state
- **Unsafe `as Drawable` cast (TvInputExtensions)** — now returns nullable `Drawable?`; no more `TypeCastException` on unknown input types

#### Performance
- **SharingStarted.WhileSubscribed(5000)** — replaced `Eagerly` in `AppsTabViewModel`; upstream collection stops when the tab is not visible
- **N+1 channel subscriptions eliminated** — single `StateFlow<Map<channelId, programs>>` in `HomeTabViewModel` replaces one `collectAsState()` per visible channel row
- **SharedPreferences caching** — six stores (`RecentAppsStore`, `LastFocusedAppStore`, `ChannelPreferences`, `AccessibilityPreferences`, `LauncherStateStore`, `PendingUpdatesStore`) now cache reads in `@Volatile` fields
- **LazyColumn keys** — all 11 `item {}` blocks in `AccessibilityOverviewScreen` now have explicit keys; no full-list recomposition on onboarding dismiss
- **Debug logging** — `Timber.DebugTree()` and `DebugLogger()` now gated behind `BuildConfig.DEBUG`
- **Coil crossfade** — `AsyncImage` crossfade enabled globally

#### Maintenance
- Kotlin 2.4.20 · Compose 1.12.1 · Coil 2.7.0 · SQLDelight 2.3.2 · Koin 4.2.2
