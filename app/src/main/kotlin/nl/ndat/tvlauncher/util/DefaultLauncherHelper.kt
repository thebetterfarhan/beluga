package nl.ndat.tvlauncher.util

import android.content.Context
import android.content.Intent
import android.provider.Settings

class DefaultLauncherHelper(
	private val context: Context,
) {
	private val packageManager = context.packageManager

	fun isDefaultLauncher(): Boolean {
		val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
		val resolvedInfo = packageManager.resolveActivity(homeIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
		return resolvedInfo?.activityInfo?.packageName == context.packageName
	}

	fun canRequestDefaultLauncher(): Boolean = true

	fun requestDefaultLauncherIntent(): Intent {
		return Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	}
}
