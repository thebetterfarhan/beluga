package nl.ndat.tvlauncher.util

import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.content.getSystemService

class DefaultLauncherHelper(
	private val context: Context,
) {
	private val roleManager by lazy { context.getSystemService<RoleManager>() }
	private val isRoleManagerAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && roleManager != null

	fun isDefaultLauncher(): Boolean {
		if (!isRoleManagerAvailable) return false
		@SuppressLint("NewApi")
		return roleManager!!.isRoleHeld(RoleManager.ROLE_HOME)
	}

	fun canRequestDefaultLauncher(): Boolean {
		if (!isRoleManagerAvailable) return false
		@SuppressLint("NewApi")
		return roleManager!!.isRoleAvailable(RoleManager.ROLE_HOME)
	}

	fun requestDefaultLauncherIntent(): Intent? {
		if (isRoleManagerAvailable) {
			@SuppressLint("NewApi")
			return roleManager!!.createRequestRoleIntent(RoleManager.ROLE_HOME)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}
		return Intent(Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	}
}
