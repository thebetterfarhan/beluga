package nl.ndat.tvlauncher.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * State inspection and manage-page entry for the stock Google TV launcher.
 *
 * Android does not allow a regular app to change another package's enabled
 * state (that requires device-owner or system privileges), so this helper only
 * observes the state and hands the user to the system App-Info page, where
 * Disable / Enable is corrected with the user's confirmation.
 */
class GoogleTvLauncherHelper(
	private val context: Context,
) {
	companion object {
		const val PACKAGE_NAME = "com.google.android.apps.tv.launcherx"
	}

	val isInstalled: Boolean by lazy {
		runCatching {
			context.packageManager.getApplicationInfo(PACKAGE_NAME, 0)
		}.isSuccess || context.packageManager.getInstalledPackages(0)
			.any { it.packageName == PACKAGE_NAME }
	}

	fun isDisabledByUser(): Boolean = context.packageManager
		.getApplicationEnabledSetting(PACKAGE_NAME)
		.let { state ->
			state == android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
				state == android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
		}

	/**
	 * Whether the stock launcher is the launcher the system would resolve for
	 * the Home button right now. Home resolution follows the preferred-activity
	 * record, which can point at the stock launcher even while the HOME role is
	 * held elsewhere, and it is also the outcome the system uses to decide
	 * whether the App-Info page offers the Disable action.
	 */
	fun resolvesAsHome(): Boolean {
		val resolved = context.packageManager.resolveActivity(
			Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
			android.content.pm.PackageManager.MATCH_DEFAULT_ONLY,
		)
		return resolved?.activityInfo?.packageName == PACKAGE_NAME
	}

	/**
	 * Opens the system App-Info page for the stock launcher. The page exposes
	 * the Disable / Enable action guarded by the system's own confirmation.
	 */
	fun appDetailsIntent(): Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
		.setData(Uri.fromParts("package", PACKAGE_NAME, null))
}
