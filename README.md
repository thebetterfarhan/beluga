# Balooga

Balooga is an accessibility-focused Android TV launcher for Chromecast with Google TV, Nvidia Shield, and other Android TV devices. It replaces the default Leanback or Google TV launcher with a fully TalkBack-compatible interface built around D-pad navigation.

<img src="https://user-images.githubusercontent.com/2305178/186512479-e94bf85d-ac09-4f9d-b54e-24bcf43c82da.png" height="400" />

## Features

### Home Screen
- **Continue** — resumes the last focused app
- **Recent** — last 4 opened apps
- **Favorites** — user-bookmarked apps, reorderable via long-press
- **Watch Next & Channels** — live TV program rows from the system TV provider

### All Apps
- Full grid of installed launchable apps
- Search by name or package name
- Long-press any app card for: Favorite / Hide / Move options

### Accessibility (built-in, no setup required)
- Full D-pad navigation with visible focus indicators
- TalkBack announces: tab structure, app names, row contents, and app launch actions
- **Focus Restoration** — choose where focus goes after leaving an app: Last focused app · First favorite · Home tab · All Apps tab
- **Onboarding card** — shown once on first launch, explains D-pad controls
- **System accessibility shortcuts** — direct links to TalkBack, audio descriptions, and high-contrast text settings from the Accessibility screen
- **Hidden apps** — hide apps from the All Apps grid without uninstalling
- **Home layout preferences** — show or hide Continue, Recent, and Watch Next rows
- **Recently updated apps card** — Play Store link to updated apps from the Accessibility screen

### Settings
Accessible via the toolbar gear icon:

| Setting | Description |
|---|---|
| Focus restoration | Choose startup focus behavior |
| Orientation help | Learn D-pad navigation and long-press menus |
| Hidden apps | Manage hidden apps list |
| Home layout | Toggle Home screen rows |
| App language | Open system per-app language settings |
| Channel order | Drag to reorder TV channel rows |
| System accessibility | TalkBack, audio descriptions, high-contrast text |
| Recently updated apps | Link to Play Store updates |

## Platform Limitations

These cannot be implemented due to Android TV platform restrictions:

- **Wallpapers** — `WallpaperManager` has no implementation on Android TV
- **Device power controls** — `goToSleep()`, `shutdown()`, `reboot()` require signature permission
- **Accessibility announcements** — `AccessibilityManager.announceForAccessibility()` is not reliably available in the project's AndroidX Core version on API 33+

## Supported Devices

- Chromecast with Google TV (all generations)
- Nvidia Shield (2015 / 2017 / Pro)
- Other Android TV devices running Android 6.0+ (API 23)

## Building

Requires Java 21 and Android SDK 37.

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

For release signing, add `keystore.properties` at the project root with:

```properties
 keystorePath=/path/to/keystore.jks
 keystorePassword=...
 keyAlias=...
 keyPassword=...
```

## Changelog

### [Unreleased]

#### Features
- **Focus restoration** — configurable startup focus target: last focused app, first favorite, Home tab, All Apps tab, or full launcher state restoration
- **Onboarding card** — first-launch TalkBack-guided introduction to D-pad navigation, shown once and dismissable
- **Accessibility overview screen** — centralized hub with direct links to system accessibility settings, audio descriptions status, high-contrast text status, and a link to recently updated apps
- **Recently updated apps tracking** — `PendingUpdatesStore` tracks app updates via `PACKAGE_REPLACED` broadcasts with a 24-hour expiry window; count announced when entering the All Apps tab; Play Store link card in the Accessibility screen
- **Package change receiver** — updates app list on `PACKAGE_ADDED`, `PACKAGE_CHANGED`, `PACKAGE_REPLACED`, and `PACKAGE_REMOVED` broadcasts; also refreshes on `BOOT_COMPLETED`

#### Bug Fixes
- **Cursor leak in ChannelResolver** — all three cursor-iterating methods (`getPreviewChannels`, `getChannelPrograms`, `getWatchNextPrograms`) now use `cursor.use {}` to guarantee cleanup on exception
- **Silent exception swallowing in PackageChangeReceiver** — empty catch block replaced with `Timber.e()` logging for actionable error visibility
- **Race condition in HiddenAppsViewModel** — removed manual `_hiddenApps` mutation; list is now purely derived from `combine(allApps, hiddenAppIds).stateIn()` so the DB flow never overwrites user unhide actions
- **Race condition in ChannelPreferencesViewModel** — channel list is now purely derived from the database flow; manual reorder operations update SharedPreferences without mutating the displayed list directly
- **Unsafe `as Drawable` cast in TvInputExtensions** — changed to nullable return type; no more `TypeCastException` risk on custom input types

#### Performance
- **SharingStarted.WhileSubscribed** — replaced `SharingStarted.Eagerly` with `WhileSubscribed(5000)` in `AppsTabViewModel`; upstream collection stops when the UI is not observing
- **N+1 channel flow subscriptions** — replaced per-row `collectAsState()` calls inside `LazyColumn.items` (one per visible channel) with a single `channelProgramsMap: StateFlow<Map<channelId, programs>>` computed in `HomeTabViewModel`
- **SharedPreferences caching** — `RecentAppsStore`, `LastFocusedAppStore`, `ChannelPreferences`, `AccessibilityPreferences`, `LauncherStateStore`, and `PendingUpdatesStore` now cache reads in `@Volatile` fields; no repeated disk I/O or JSON parsing on every call
- **LazyColumn keys** — all 11 `item {}` blocks in `AccessibilityOverviewScreen` now have explicit `key` parameters, preventing full-list recomposition when the conditional onboarding card is shown/dismissed
- **Debug logging** — `Timber.DebugTree()` and `DebugLogger()` now gated behind `BuildConfig.DEBUG`; no log spam in release builds
- **Coil crossfade** — `AsyncImage` crossfade enabled globally in `ImageLoader`

#### Maintenance
- Upgraded Kotlin to 2.4.20, Compose to 1.12.1, Coil to 2.7.0, SQLDelight to 2.3.2, Koin to 4.2.2
- Added `lifecycle-runtime-compose` dependency for `collectAsStateWithLifecycle` migration path
- Migrated remaining `@Composable val LocalLifecycleOwner` usages to the `lifecycle-runtime-compose` equivalent (deprecated warning resolution)

### [Prior releases]

See the git history for earlier changes.
