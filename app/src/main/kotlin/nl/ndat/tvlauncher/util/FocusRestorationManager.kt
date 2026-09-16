package nl.ndat.tvlauncher.util

import nl.ndat.tvlauncher.data.sqldelight.App

enum class StartupFocusTarget {
	FAVORITE,
	HOME_TAB,
	ALL_APPS_TAB,
}

fun interface FocusRestoreModeProvider {
	fun focusRestoreMode(): FocusRestoreMode
}

class FocusRestorationManager(private val modeProvider: FocusRestoreModeProvider) {
	constructor(preferences: AccessibilityPreferences) : this(modeProvider = preferences)

	fun startupFocusTarget() = when (modeProvider.focusRestoreMode()) {
		FocusRestoreMode.HOME_TAB -> StartupFocusTarget.HOME_TAB
		FocusRestoreMode.ALL_APPS_TAB -> StartupFocusTarget.ALL_APPS_TAB
		FocusRestoreMode.LAST_FOCUSED_APP,
		FocusRestoreMode.FIRST_FAVORITE,
		FocusRestoreMode.COMPLETE_LAUNCHER_STATE -> StartupFocusTarget.FAVORITE
	}

	fun restoresLauncherState(): Boolean =
		modeProvider.focusRestoreMode() == FocusRestoreMode.COMPLETE_LAUNCHER_STATE

	fun preferredFavoriteIndex(apps: List<App>, lastFocusedAppId: String?): Int = when (modeProvider.focusRestoreMode()) {
		FocusRestoreMode.LAST_FOCUSED_APP -> apps.indexOfFirst { it.id == lastFocusedAppId }.coerceAtLeast(0)
		FocusRestoreMode.FIRST_FAVORITE -> 0
		FocusRestoreMode.HOME_TAB,
		// TODO: Persist and restore tab, focused item, scroll position, and expanded sections.
		FocusRestoreMode.ALL_APPS_TAB,
		FocusRestoreMode.COMPLETE_LAUNCHER_STATE -> 0
	}
}
