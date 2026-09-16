package nl.ndat.tvlauncher.util

import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

class SystemAccessibilityHelper(private val context: Context) {

	private val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

	val isScreenReaderEnabled: Boolean
		get() {
			val enabledServices = Settings.Secure.getString(
				context.contentResolver,
				Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
			) ?: return false
			val screenReaderPackageName = context.packageName
			return enabledServices.split(":").any { service ->
				service.contains(screenReaderPackageName, ignoreCase = true)
			}
		}

	val isAudioDescriptionRequested: Boolean
		get() = accessibilityManager.isAudioDescriptionRequested

	val isHighContrastTextEnabled: Boolean
		get() = Settings.Secure.getInt(
			context.contentResolver,
			"accessibility_high_text_contrast_enabled",
			0
		) == 1

	val isTalkBackEnabled: Boolean
		get() {
			val enabledServices = Settings.Secure.getString(
				context.contentResolver,
				Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
			) ?: return false
			val lowerServices = enabledServices.lowercase()
			return lowerServices.contains("talkback") ||
				lowerServices.contains("com.google.android.accessibility.talkback")
		}
}
