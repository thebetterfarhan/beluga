package nl.ndat.tvlauncher.util

import android.content.Context
import kotlinx.serialization.json.Json

class LauncherStateStore(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "launcher_state"))

	fun get(): LauncherStateSnapshot {
		val json = store.getString("snapshot", null) ?: return LauncherStateSnapshot()
		return runCatching {
			Json.decodeFromString(LauncherStateSnapshot.serializer(), json)
		}.getOrDefault(LauncherStateSnapshot())
	}

	fun set(snapshot: LauncherStateSnapshot) {
		store.setString("snapshot", Json.encodeToString(LauncherStateSnapshot.serializer(), snapshot))
	}

	fun clear() {
		store.remove("snapshot")
	}
}
