package nl.ndat.tvlauncher.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import nl.ndat.tvlauncher.LauncherActivity

/**
 * Accessibility service that intercepts the HOME button press and redirects
 * it to BlindPilot instead of the system launcher.
 *
 * This works even when the system periodically re-enables the stock launcher
 * as the default home action — the accessibility service always has priority.
 *
 * Revert: disable this service in system accessibility settings.
 */
class HomeRemapAccessibilityService : AccessibilityService() {

	override fun onAccessibilityEvent(event: AccessibilityEvent?) {
	}

	override fun onKeyEvent(event: KeyEvent?): Boolean {
		if (event == null) return false
		if (event.action != KeyEvent.ACTION_DOWN) return false
		if (event.keyCode != KeyEvent.KEYCODE_HOME) return false

		val launcherIntent = Intent(this, LauncherActivity::class.java).apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
		}
		startActivity(launcherIntent)
		return true
	}

	override fun onInterrupt() {
		// Required override; not used but must be present
	}
}
