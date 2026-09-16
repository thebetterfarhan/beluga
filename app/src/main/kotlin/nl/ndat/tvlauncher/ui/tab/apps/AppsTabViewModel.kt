package nl.ndat.tvlauncher.ui.tab.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
	private val allApps = appRepository.getApps()
		// Hide launcher app from showing
		.map { apps -> apps.filterNot { app -> app.packageName == BuildConfig.APPLICATION_ID } }
		.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	private val _searchQuery = MutableStateFlow("")
	val searchQuery = _searchQuery.asStateFlow()

	val apps = combine(allApps, _searchQuery) { apps, query ->
		if (query.isBlank()) apps
		else apps.filter { app ->
			app.displayName.contains(query, ignoreCase = true) ||
				app.packageName.contains(query, ignoreCase = true)
		}
	}.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	val searchResultCount: Int
		get() = apps.value.size

	fun onSearchQueryChange(query: String) {
		_searchQuery.value = query
	}

	fun clearSearch() {
		_searchQuery.value = ""
	}

	fun rememberFocusedApp(appId: String) = launcherStateRecorder.recordFocusedItem(appId)

	fun appsScrollIndex() = launcherStateRecorder.scrollOffset(LauncherStateScrollPositions.APPS_TAB)
	fun rememberAppsScrollIndex(firstVisibleItemIndex: Int) =
		launcherStateRecorder.recordScrollOffset(LauncherStateScrollPositions.APPS_TAB, firstVisibleItemIndex)

	fun favoriteApp(app: App, favorite: Boolean) = viewModelScope.launch {
		if (favorite) appRepository.favorite(app.id)
		else appRepository.unfavorite(app.id)
	}
}
