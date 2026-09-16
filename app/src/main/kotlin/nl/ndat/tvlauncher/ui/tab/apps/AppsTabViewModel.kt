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
import nl.ndat.tvlauncher.util.HiddenAppsStore
import nl.ndat.tvlauncher.util.LauncherStateRecorder
import nl.ndat.tvlauncher.util.LauncherStateScrollPositions
import nl.ndat.tvlauncher.util.PendingUpdatesStore

class AppsTabViewModel(
	private val appRepository: AppRepository,
	private val launcherStateRecorder: LauncherStateRecorder,
	private val hiddenAppsStore: HiddenAppsStore,
	private val pendingUpdatesStore: PendingUpdatesStore,
) : ViewModel() {
	private val allApps = appRepository.getApps()
		.map { apps -> apps.filterNot { app -> app.packageName == BuildConfig.APPLICATION_ID } }
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

	private val hiddenIds = MutableStateFlow(hiddenAppsStore.get())

	private val _searchQuery = MutableStateFlow("")
	val searchQuery = _searchQuery.asStateFlow()

	val apps = combine(allApps, hiddenIds, _searchQuery) { apps, hidden, query ->
		val visible = apps.filterNot { it.id in hidden }
		if (query.isBlank()) visible
		else visible.filter { app ->
			app.displayName.contains(query, ignoreCase = true) ||
				app.packageName.contains(query, ignoreCase = true)
		}
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

	val searchResultCount: Int
		get() = apps.value.size

	val recentlyUpdatedCount: Int
		get() = pendingUpdatesStore.getRecentlyUpdatedCount()

	fun onSearchQueryChange(query: String) {
		_searchQuery.value = query
	}

	fun clearSearch() {
		_searchQuery.value = ""
	}

	fun isHidden(appId: String): Boolean = appId in hiddenIds.value

	fun hideApp(appId: String) {
		hiddenIds.value = hiddenAppsStore.hide(appId)
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
