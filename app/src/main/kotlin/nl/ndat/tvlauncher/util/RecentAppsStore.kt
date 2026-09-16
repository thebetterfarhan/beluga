package nl.ndat.tvlauncher.util

import android.content.Context
import androidx.compose.runtime.Immutable

/**
 * MRU list of recently opened app package identifiers, kept under a
 * bounded size cap. Distinct from the single-value [LastFocusedAppStore]
 * which only drives focus restoration.
 *
 * Backed by a single shared-preferences string slot. The list is ordered
 * most-recent-first; adding an existing entry moves it to the head and
 * preserves distinctness.
 *
 * Caches the parsed list in memory to avoid repeated SharedPreferences reads.
 */
@Immutable
class RecentAppsStore(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "recent_apps"))

	@Volatile
	private var cached: List<String>? = null

	fun get(): List<String> {
		cached?.let { return it }
		val raw = store.getString("recent_app_ids", null) ?: return emptyList<String>().also { cached = it }
		val parsed = raw.split(MRU_DELIMITER).filter(String::isNotEmpty)
		cached = parsed
		return parsed
	}

	fun add(appId: String) {
		if (appId.isEmpty()) return
		val existing = get().toMutableList()
		existing.remove(appId)
		existing.add(0, appId)
		val trimmed = if (existing.size > MRU_CAP) existing.take(MRU_CAP) else existing
		store.setString("recent_app_ids", trimmed.joinToString(MRU_DELIMITER))
		cached = trimmed
	}

	fun clear() {
		store.remove("recent_app_ids")
		cached = emptyList()
	}

	companion object {
		private const val MRU_DELIMITER = "\u241E" // unit separator
		const val MRU_CAP = 8
	}
}
