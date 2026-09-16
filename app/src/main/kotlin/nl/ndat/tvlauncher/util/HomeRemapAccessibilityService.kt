package nl.ndat.tvlauncher.util

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog

class HomeRemapAccessibilityService : AccessibilityService() {

	override fun onAccessibilityEvent(event: AccessibilityEvent) { }

	override fun onKeyEvent(event: KeyEvent): Boolean {
		if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_HOME) {
			debugLauncherLog("home-remap: intercepted HOME key")
			val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
			if (launchIntent != null) {
				startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
			}
			return true
		}
		return false
	}

	override fun onServiceConnected() {
		super.onServiceConnected()
		debugLauncherLog("home-remap: service connected")
	}

	override fun onInterrupt() {
	}
}

