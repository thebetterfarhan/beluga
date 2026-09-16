package nl.ndat.tvlauncher.util

import android.content.Context
import android.content.SharedPreferences

class PendingUpdatesStore(private val context: Context) {

	companion object {
		private const val PREFS_NAME = "pending_updates"
		private const val KEY_RECENTLY_UPDATED = "recently_updated"
		private const val UPDATED_COUNT_CUTOFF_MS = 24L * 60 * 60 * 1000
	}

	private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

	fun recordUpdated(packageName: String, versionCode: Long) {
		val now = System.currentTimeMillis()
		val entry = "$packageName:$versionCode:$now"
		val existing = prefs.getStringSet(KEY_RECENTLY_UPDATED, emptySet())?.toMutableSet() ?: mutableSetOf()
		existing.add(entry)
		prefs.edit().putStringSet(KEY_RECENTLY_UPDATED, existing).apply()
	}

	fun getRecentlyUpdatedCount(): Int {
		val now = System.currentTimeMillis()
		val entries = prefs.getStringSet(KEY_RECENTLY_UPDATED, emptySet())?.toMutableSet() ?: mutableSetOf()
		val iterator = entries.iterator()
		while (iterator.hasNext()) {
			val entry = iterator.next()
			val parts = entry.split(":")
			val timestamp = parts.getOrNull(2)?.toLongOrNull() ?: 0L
			if (now - timestamp > UPDATED_COUNT_CUTOFF_MS) {
				iterator.remove()
			}
		}
		prefs.edit().putStringSet(KEY_RECENTLY_UPDATED, entries).apply()
		return entries.size
	}

	fun getRecentlyUpdatedPackages(): Set<String> {
		return prefs.getStringSet(KEY_RECENTLY_UPDATED, emptySet())
			?.mapNotNull { it.split(":").getOrNull(0) }
			?.toSet()
			?: emptySet()
	}
}
