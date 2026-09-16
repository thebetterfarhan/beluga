package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.lifecycle.ViewModel
import nl.ndat.tvlauncher.util.HomePreferences

class HomePreferencesViewModel(
	private val preferences: HomePreferences,
) : ViewModel() {
	val showContinue = preferences.showContinue
	val showRecent = preferences.showRecent
	val showWatchNext = preferences.showWatchNext

	fun setShowContinue(show: Boolean) = preferences.setShowContinue(show)
	fun setShowRecent(show: Boolean) = preferences.setShowRecent(show)
	fun setShowWatchNext(show: Boolean) = preferences.setShowWatchNext(show)
}
