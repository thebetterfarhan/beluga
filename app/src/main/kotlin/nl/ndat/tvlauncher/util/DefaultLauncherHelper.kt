package nl.ndat.tvlauncher.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings

class DefaultLauncherHelper(
	private val context: Context,
) {
	companion object {
		private const val PREFS_NAME = "default_launcher_prefs"
		private const val KEY_HAS_PROMPTED = "has_prompted_default_launcher"
	}

	private val packageManager = context.packageManager
	private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

	fun isDefaultLauncher(): Boolean {
		val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
		val resolvedInfo = packageManager.resolveActivity(homeIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
		return resolvedInfo?.activityInfo?.packageName == context.packageName
	}

	fun canRequestDefaultLauncher(): Boolean = true

	/**
	 * Returns true if we should show the default launcher prompt.
	 * Only returns true once per install — subsequent calls return false
	 * even if the user hasn't set Balooga as default.
	 */
	fun shouldPromptDefaultLauncher(): Boolean {
		if (isDefaultLauncher()) return false
		val hasPrompted = prefs.getBoolean(KEY_HAS_PROMPTED, false)
		if (!hasPrompted) {
			prefs.edit().putBoolean(KEY_HAS_PROMPTED, true).apply()
		}
		return !hasPrompted
	}

	fun requestDefaultLauncherIntent(): Intent {
		return Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	}
}
