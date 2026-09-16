package nl.ndat.tvlauncher.util

import android.content.Context
import kotlinx.serialization.json.Json

class LauncherStateStore(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "launcher_state"))

	@Volatile
	private var cached: LauncherStateSnapshot? = null

	fun get(): LauncherStateSnapshot {
		cached?.let { return it }
		val json = store.getString("snapshot", null)
		if (json == null) {
			val empty = LauncherStateSnapshot()
			cached = empty
			return empty
		}
		return runCatching {
			Json.decodeFromString(LauncherStateSnapshot.serializer(), json)
		}.getOrDefault(LauncherStateSnapshot()).also { cached = it }
	}

	fun set(snapshot: LauncherStateSnapshot) {
		store.setString("snapshot", Json.encodeToString(LauncherStateSnapshot.serializer(), snapshot))
		cached = snapshot
	}

	fun clear() {
		store.remove("snapshot")
		cached = LauncherStateSnapshot()
	}
}
