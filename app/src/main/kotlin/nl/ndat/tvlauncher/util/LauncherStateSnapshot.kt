package nl.ndat.tvlauncher.util

import kotlinx.serialization.Serializable

@Serializable
data class LauncherStateSnapshot(
	val version: Int = CurrentLauncherStateVersion,
	val destination: SavedDestination = SavedDestination.HOME,
	val focusedItemId: String? = null,
	val scrollOffsets: Map<String, Int> = emptyMap(),
	val expandedSectionIds: Set<String> = emptySet(),
)

const val CurrentLauncherStateVersion = 1

object LauncherStateScrollPositions {
	const val HOME_TAB = "home_tab"
	const val APPS_TAB = "apps_tab"
}

enum class SavedDestination {
	HOME,
	APPS,
	ACCESSIBILITY,
	FOCUS_RESTORATION,
	ORIENTATION_HELP,
}
