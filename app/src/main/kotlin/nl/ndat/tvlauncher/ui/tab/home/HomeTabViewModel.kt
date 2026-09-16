package nl.ndat.tvlauncher.ui.tab.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.ndat.tvlauncher.data.repository.AppRepository
import nl.ndat.tvlauncher.data.repository.ChannelRepository
import nl.ndat.tvlauncher.data.sqldelight.App
import nl.ndat.tvlauncher.data.sqldelight.Channel
import nl.ndat.tvlauncher.util.LastFocusedAppStore
import nl.ndat.tvlauncher.util.LauncherStateRecorder
import nl.ndat.tvlauncher.util.LauncherStateScrollPositions
import nl.ndat.tvlauncher.util.FocusRestorationManager
import nl.ndat.tvlauncher.util.RecentAppsStore

class HomeTabViewModel(
	private val appRepository: AppRepository,
	private val channelRepository: ChannelRepository,
	private val lastFocusedAppStore: LastFocusedAppStore,
	private val recentAppsStore: RecentAppsStore,
	private val focusRestorationManager: FocusRestorationManager,
	private val launcherStateRecorder: LauncherStateRecorder,
) : ViewModel() {
	val lastFocusedAppId = lastFocusedAppStore.id
	fun preferredFavoriteIndex(apps: List<App>) =
		focusRestorationManager.preferredFavoriteIndex(apps, lastFocusedAppId.value)
	private var storedFocusedAppId = lastFocusedAppId.value

	fun rememberFocusedApp(appId: String) {
		if (storedFocusedAppId == appId) return
		storedFocusedAppId = appId
		launcherStateRecorder.recordFocusedItem(appId)
		viewModelScope.launch(Dispatchers.IO) { lastFocusedAppStore.set(appId) }
	}

	fun homeScrollIndex() = launcherStateRecorder.scrollOffset(LauncherStateScrollPositions.HOME_TAB)
	fun rememberHomeScrollIndex(firstVisibleItemIndex: Int) =
		launcherStateRecorder.recordScrollOffset(LauncherStateScrollPositions.HOME_TAB, firstVisibleItemIndex)

	private val recentAppIdsFlow = MutableStateFlow(recentAppsStore.get())
	val recentAppIds = recentAppIdsFlow.asStateFlow()

	val apps = appRepository.getFavoriteApps()
		.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	val allApps = appRepository.getApps()
		.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	val channels = channelRepository.getFavoriteAppChannels()
		.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	val watchNextPrograms = channelRepository.getWatchNextPrograms()
		.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	fun channelPrograms(channel: Channel) = channelRepository.getProgramsByChannel(channel)

	fun favoriteApp(app: App, favorite: Boolean) = viewModelScope.launch {
		if ((app.favoriteOrder != null) == favorite) return@launch

		if (favorite) appRepository.favorite(app.id)
		else appRepository.unfavorite(app.id)
	}

	fun setFavoriteOrder(app: App, order: Long) = viewModelScope.launch {
		// Make sure app is favorite first
		if (app.favoriteOrder == null) appRepository.favorite(app.id)
		appRepository.updateFavoriteOrder(app.id, order)
	}

	fun recordOpenedApp(appId: String) {
		if (appId.isEmpty()) return
		viewModelScope.launch(Dispatchers.IO) {
			recentAppsStore.add(appId)
			recentAppIdsFlow.value = recentAppsStore.get()
		}
	}
}
