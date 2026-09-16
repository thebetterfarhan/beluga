package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import nl.ndat.tvlauncher.data.repository.AppRepository
import nl.ndat.tvlauncher.data.sqldelight.App
import nl.ndat.tvlauncher.util.HiddenAppsStore

class HiddenAppsViewModel(
	private val appRepository: AppRepository,
	private val hiddenAppsStore: HiddenAppsStore,
) : ViewModel() {
	private val _hiddenAppIds = MutableStateFlow(hiddenAppsStore.get())
	val hiddenAppIds = _hiddenAppIds.asStateFlow()

	// Purely derived: no manual writes to this flow. Every update comes from
	// the upstream appRepository flow or _hiddenAppIds mutations.
	val hiddenApps = combine(
		appRepository.getApps(),
		_hiddenAppIds,
	) { allApps, hiddenIds ->
		allApps.filter { it.id in hiddenIds }
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

	fun unhide(appId: String) {
		_hiddenAppIds.value = hiddenAppsStore.unhide(appId)
	}

	fun unhideAll() {
		hiddenAppsStore.unhideAll()
		_hiddenAppIds.value = emptySet()
	}
}
