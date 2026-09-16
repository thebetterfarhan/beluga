package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.ndat.tvlauncher.data.repository.AppRepository
import nl.ndat.tvlauncher.data.sqldelight.App
import nl.ndat.tvlauncher.util.HiddenAppsStore

class HiddenAppsViewModel(
	private val appRepository: AppRepository,
	private val hiddenAppsStore: HiddenAppsStore,
) : ViewModel() {
	private val _hiddenAppIds = MutableStateFlow(hiddenAppsStore.get())
	val hiddenAppIds = _hiddenAppIds.asStateFlow()

	private val _hiddenApps = MutableStateFlow<List<App>>(emptyList())
	val hiddenApps = _hiddenApps.asStateFlow()

	init {
		loadHiddenApps()
	}

	private fun loadHiddenApps() {
		viewModelScope.launch {
			appRepository.getApps().collect { allApps ->
				_hiddenApps.value = allApps.filter { it.id in _hiddenAppIds.value }
			}
		}
	}

	fun unhide(appId: String) {
		_hiddenAppIds.value = hiddenAppsStore.unhide(appId)
		_hiddenApps.value = _hiddenApps.value.filterNot { it.id == appId }
	}

	fun unhideAll() {
		hiddenAppsStore.unhideAll()
		_hiddenAppIds.value = emptySet()
		_hiddenApps.value = emptyList()
	}
}
