package nl.ndat.tvlauncher.ui.screen.launcher

import androidx.lifecycle.ViewModel
import nl.ndat.tvlauncher.data.Destination
import nl.ndat.tvlauncher.data.Destinations
import nl.ndat.tvlauncher.util.FocusRestorationManager
import nl.ndat.tvlauncher.util.LauncherStateRecorder
import nl.ndat.tvlauncher.util.SavedDestination
import nl.ndat.tvlauncher.util.StartupFocusTarget
import nl.ndat.tvlauncher.util.isTabDestination
import nl.ndat.tvlauncher.util.toDestination
import nl.ndat.tvlauncher.util.toSavedDestination

class LauncherScreenViewModel(
	focusRestorationManager: FocusRestorationManager,
	private val launcherStateRecorder: LauncherStateRecorder,
) : ViewModel() {
	private val modeFocusTarget = focusRestorationManager.startupFocusTarget()

	private val savedTabDestination: SavedDestination? =
		if (focusRestorationManager.restoresLauncherState()) {
			launcherStateRecorder.snapshot.destination.takeIf { it.isTabDestination() }
		} else null

	val startupDestination: Destination = savedTabDestination?.toDestination()
		?: if (modeFocusTarget == StartupFocusTarget.ALL_APPS_TAB) Destinations.Apps else Destinations.Home

	val startupFocusTarget: StartupFocusTarget =
		if (savedTabDestination == SavedDestination.APPS) StartupFocusTarget.ALL_APPS_TAB else modeFocusTarget

	val startupItemId: String? = savedTabDestination?.let { launcherStateRecorder.snapshot.focusedItemId }

	fun rememberDestination(destination: Destination) {
		val saved = destination.toSavedDestination()
		if (!saved.isTabDestination()) return
		launcherStateRecorder.recordDestination(saved)
	}
}
