package nl.ndat.tvlauncher.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LastFocusedAppStore(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "launcher_focus"))

	private val _id = MutableStateFlow(get())
	val id: StateFlow<String?> = _id.asStateFlow()

	fun get(): String? = store.getString("last_focused_app_id", null)
	fun set(appId: String) {
		store.setString("last_focused_app_id", appId)
		_id.value = appId
	}
}
