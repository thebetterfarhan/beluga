package nl.ndat.tvlauncher.util

import android.content.Context

class HomePreferences(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "home_preferences"))

	fun showContinue() = store.getBoolean(KEY_SHOW_CONTINUE, defaultValue = true)
	fun setShowContinue(show: Boolean) = store.setBoolean(KEY_SHOW_CONTINUE, show)

	fun showRecent() = store.getBoolean(KEY_SHOW_RECENT, defaultValue = true)
	fun setShowRecent(show: Boolean) = store.setBoolean(KEY_SHOW_RECENT, show)

	fun showWatchNext() = store.getBoolean(KEY_SHOW_WATCH_NEXT, defaultValue = true)
	fun setShowWatchNext(show: Boolean) = store.setBoolean(KEY_SHOW_WATCH_NEXT, show)

	companion object {
		const val KEY_SHOW_CONTINUE = "show_continue"
		const val KEY_SHOW_RECENT = "show_recent"
		const val KEY_SHOW_WATCH_NEXT = "show_watch_next"
	}
}
