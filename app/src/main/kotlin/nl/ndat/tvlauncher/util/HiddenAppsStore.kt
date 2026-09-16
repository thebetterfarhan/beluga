package nl.ndat.tvlauncher.util

import android.content.Context
import androidx.compose.runtime.Immutable

/**
 * Set of app package identifiers that the user has chosen to hide from
 * the All Apps grid. Hidden apps remain installed and usable via deep-link
 * or if their package is known — they are simply not surfaced in the grid.
 *
 * Backed by a single shared-preferences string slot. The set is stored as
 * a delimited string; empty means no apps are hidden.
 *
 * The parsed set is cached in memory and only re-parsed on mutations,
 * avoiding repeated string splitting on every [get] or [isHidden] call.
 */
@Immutable
class HiddenAppsStore(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "hidden_apps"))

	@Volatile
	private var cached: Set<String>? = null

	private fun parse(): Set<String> = cached ?: run {
		val raw = store.getString(KEY, null)
		val parsed = if (raw == null) {
			emptySet()
		} else {
			raw.split(DELIMITER).filter(String::isNotEmpty).toSet()
		}
		cached = parsed
		parsed
	}

	fun get(): Set<String> = parse()

	fun isHidden(appId: String): Boolean = parse().contains(appId)

	fun hide(appId: String): Set<String> {
		if (appId.isEmpty()) return parse()
		val updated = parse() + appId
		cached = updated
		store.setString(KEY, updated.joinToString(DELIMITER))
		return updated
	}

	fun unhide(appId: String): Set<String> {
		val updated = parse() - appId
		cached = updated
		if (updated.isEmpty()) store.remove(KEY)
		else store.setString(KEY, updated.joinToString(DELIMITER))
		return updated
	}

	fun unhideAll(): Set<String> {
		cached = emptySet()
		store.remove(KEY)
		return emptySet()
	}

	companion object {
		private const val KEY = "hidden_app_ids"
		private const val DELIMITER = "\u241E" // unit separator
	}
}
