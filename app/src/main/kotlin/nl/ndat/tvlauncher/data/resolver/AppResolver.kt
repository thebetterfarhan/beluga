package nl.ndat.tvlauncher.data.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.ResolveInfoFlags
import android.content.pm.ResolveInfo
import android.os.Build
import nl.ndat.tvlauncher.data.sqldelight.App

class AppResolver {
	companion object {
		private val launcherCategories = arrayOf(
			Intent.CATEGORY_LEANBACK_LAUNCHER,
			Intent.CATEGORY_LAUNCHER,
		)

		const val APP_ID_PREFIX = "app:"
	}

	fun getApplication(context: Context, packageId: String): App? {
		val packageManager = context.packageManager

		return getLaunchableActivities(packageManager, packageId).firstOrNull()?.toApp(packageManager)
	}

	fun getApplications(context: Context): List<App> {
		val packageManager = context.packageManager

		return getLaunchableActivities(packageManager)
			.map { it.toApp(packageManager) }
	}

	private fun getLaunchableActivities(
		packageManager: PackageManager,
		packageId: String? = null,
	): List<ResolveInfo> = launcherCategories
		.flatMap { category ->
			val intent = Intent(Intent.ACTION_MAIN, null)
				.addCategory(category)
				.apply { if (packageId != null) setPackage(packageId) }
			packageManager.queryIntentActivities(intent)
		}
		// Leanback entries are queried first. Keep their actual activity rather
		// than replacing it with the package's generic launch intent.
		.distinctBy { it.activityInfo.packageName }

	private fun PackageManager.queryIntentActivities(intent: Intent) = when {
		Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
			queryIntentActivities(intent, ResolveInfoFlags.of(0L))

		else ->
			queryIntentActivities(intent, 0)
	}

	private fun ResolveInfo.toApp(packageManager: PackageManager) = App(
		id = "$APP_ID_PREFIX${activityInfo.packageName}",

		displayName = activityInfo.loadLabel(packageManager).toString(),
		packageName = activityInfo.packageName,

		launchIntentUriDefault = Intent(Intent.ACTION_MAIN)
			.setClassName(activityInfo.packageName, activityInfo.name)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			.toUri(0),
		launchIntentUriLeanback = null,

		favoriteOrder = null,
	)
}
