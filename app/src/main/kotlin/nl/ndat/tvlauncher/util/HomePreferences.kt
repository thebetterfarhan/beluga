package nl.ndat.tvlauncher.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomePreferences(private val prefs: SharedPreferences) {
	constructor(context: Context) : this(
		context.getSharedPreferences("home_preferences", Context.MODE_PRIVATE)
	)

	private val _showContinue = MutableStateFlow(prefs.getBoolean(KEY_SHOW_CONTINUE, true))
	val showContinue: StateFlow<Boolean> = _showContinue.asStateFlow()
	fun setShowContinue(show: Boolean) {
		_showContinue.value = show
		prefs.edit().putBoolean(KEY_SHOW_CONTINUE, show).apply()
	}

	private val _showRecent = MutableStateFlow(prefs.getBoolean(KEY_SHOW_RECENT, true))
	val showRecent: StateFlow<Boolean> = _showRecent.asStateFlow()
	fun setShowRecent(show: Boolean) {
		_showRecent.value = show
		prefs.edit().putBoolean(KEY_SHOW_RECENT, show).apply()
	}

	private val _showWatchNext = MutableStateFlow(prefs.getBoolean(KEY_SHOW_WATCH_NEXT, true))
	val showWatchNext: StateFlow<Boolean> = _showWatchNext.asStateFlow()
	fun setShowWatchNext(show: Boolean) {
		_showWatchNext.value = show
		prefs.edit().putBoolean(KEY_SHOW_WATCH_NEXT, show).apply()
	}

	companion object {
		const val KEY_SHOW_CONTINUE = "show_continue"
		const val KEY_SHOW_RECENT = "show_recent"
		const val KEY_SHOW_WATCH_NEXT = "show_watch_next"
	}
}
