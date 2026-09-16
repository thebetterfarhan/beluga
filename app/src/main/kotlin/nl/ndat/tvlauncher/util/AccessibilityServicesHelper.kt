package nl.ndat.tvlauncher.util

import android.content.Context
import android.view.accessibility.AccessibilityManager

object AccessibilityServicesHelper {

	fun isTalkBackEnabled(context: Context): Boolean {
		val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
		val enabledServices = am.getEnabledAccessibilityServiceList(
			android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
		)
		return enabledServices.any { service ->
			val name = service.resolveInfo.serviceInfo.name ?: ""
			val pkg = service.resolveInfo.serviceInfo.packageName ?: ""
			pkg == "com.google.android.accessibility.talkback" ||
				name.contains("TalkBack", ignoreCase = true)
		}
	}

	fun isHomeRemapEnabled(context: Context): Boolean {
		val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
		val enabledServices = am.getEnabledAccessibilityServiceList(
			android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
		)
		return enabledServices.any { service ->
			service.resolveInfo.serviceInfo.name?.contains("HomeRemapAccessibilityService") == true
		}
	}
}

