package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.ndat.tvlauncher.util.HomePreferences

class HomePreferencesViewModel(
	private val preferences: HomePreferences,
) : ViewModel() {
	private val _showContinue = MutableStateFlow(preferences.showContinue())
	val showContinue = _showContinue.asStateFlow()

	private val _showRecent = MutableStateFlow(preferences.showRecent())
	val showRecent = _showRecent.asStateFlow()

	private val _showWatchNext = MutableStateFlow(preferences.showWatchNext())
	val showWatchNext = _showWatchNext.asStateFlow()

	fun setShowContinue(show: Boolean) {
		preferences.setShowContinue(show)
		_showContinue.value = show
	}

	fun setShowRecent(show: Boolean) {
		preferences.setShowRecent(show)
		_showRecent.value = show
	}

	fun setShowWatchNext(show: Boolean) {
		preferences.setShowWatchNext(show)
		_showWatchNext.value = show
	}
}
