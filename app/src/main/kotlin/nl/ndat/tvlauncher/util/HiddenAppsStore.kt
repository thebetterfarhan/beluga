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
 */
@Immutable
class HiddenAppsStore(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "hidden_apps"))

	fun get(): Set<String> {
		val raw = store.getString(KEY, null) ?: return emptySet()
		return raw.split(DELIMITER).filter(String::isNotEmpty).toSet()
	}

	fun isHidden(appId: String): Boolean = appId in get()

	fun hide(appId: String) {
		if (appId.isEmpty()) return
		val updated = get() + appId
		store.setString(KEY, updated.joinToString(DELIMITER))
	}

	fun unhide(appId: String) {
		val updated = get() - appId
		if (updated.isEmpty()) store.remove(KEY)
		else store.setString(KEY, updated.joinToString(DELIMITER))
	}

	fun unhideAll() {
		store.remove(KEY)
	}

	companion object {
		private const val KEY = "hidden_app_ids"
		private const val DELIMITER = "\u241E" // unit separator
	}
}
