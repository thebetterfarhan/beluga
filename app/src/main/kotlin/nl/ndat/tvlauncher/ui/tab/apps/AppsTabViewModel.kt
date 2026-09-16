package nl.ndat.tvlauncher.ui.tab.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.ndat.tvlauncher.BuildConfig
import nl.ndat.tvlauncher.data.repository.AppRepository
import nl.ndat.tvlauncher.data.sqldelight.App

import nl.ndat.tvlauncher.util.LauncherStateRecorder
import nl.ndat.tvlauncher.util.LauncherStateScrollPositions

class AppsTabViewModel(
	private val appRepository: AppRepository,
	private val launcherStateRecorder: LauncherStateRecorder,
) : ViewModel() {
	val apps = appRepository.getApps()
		// Hide launcher app from showing
		.map { apps -> apps.filterNot { app -> app.packageName == BuildConfig.APPLICATION_ID } }
		.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	fun rememberFocusedApp(appId: String) = launcherStateRecorder.recordFocusedItem(appId)

	fun appsScrollIndex() = launcherStateRecorder.scrollOffset(LauncherStateScrollPositions.APPS_TAB)
	fun rememberAppsScrollIndex(firstVisibleItemIndex: Int) =
		launcherStateRecorder.recordScrollOffset(LauncherStateScrollPositions.APPS_TAB, firstVisibleItemIndex)

	fun favoriteApp(app: App, favorite: Boolean) = viewModelScope.launch {
		if (favorite) appRepository.favorite(app.id)
		else appRepository.unfavorite(app.id)
	}
}
