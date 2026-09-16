package nl.ndat.tvlauncher.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.net.toUri
import nl.ndat.tvlauncher.data.sqldelight.App

fun App.createDrawable(context: Context): Drawable {
	val packageManager = context.packageManager
	val uri = launchIntentUriLeanback
		?.toUri()
		?: launchIntentUriDefault?.toUri()

	if (uri == Uri.EMPTY) {
		return packageManager.defaultActivityIcon
	}

	val intent = uri?.let { Intent.parseUri(it.toString(), 0) }

	return try {
		intent?.let { packageManager.getActivityBanner(it) ?: packageManager.getActivityIcon(it) }
			?: packageManager.defaultActivityIcon
	} catch (err: PackageManager.NameNotFoundException) {
		packageManager.defaultActivityIcon
	} catch (err: NullPointerException) {
		packageManager.defaultActivityIcon
	}
}
