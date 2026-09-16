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
 */
@Immutable
class RecentAppsStore(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "recent_apps"))

	fun get(): List<String> {
		val raw = store.getString("recent_app_ids", null) ?: return emptyList()
		return raw.split(MRU_DELIMITER).filter(String::isNotEmpty)
	}

	fun add(appId: String) {
		if (appId.isEmpty()) return
		val existing = get().toMutableList()
		existing.remove(appId)
		existing.add(0, appId)
		val trimmed = if (existing.size > MRU_CAP) existing.take(MRU_CAP) else existing
		store.setString("recent_app_ids", trimmed.joinToString(MRU_DELIMITER))
	}

	fun clear() {
		store.remove("recent_app_ids")
	}

	companion object {
		private const val MRU_DELIMITER = "\u241E" // unit separator
		const val MRU_CAP = 8
	}
}
