package nl.ndat.tvlauncher.util

import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
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

	fun canRequestDefaultLauncher(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			val roleManager = context.getSystemService(android.app.role.RoleManager::class.java)
			roleManager?.isRoleAvailable(RoleManager.ROLE_HOME) == true
		} else {
			true
		}
	}

	fun requestDefaultLauncherIntent(): Intent? {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			val roleManager = context.getSystemService(android.app.role.RoleManager::class.java)
			if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
				roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
			} else {
				Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
			}
		} else {
			Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
		}.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	}
}
