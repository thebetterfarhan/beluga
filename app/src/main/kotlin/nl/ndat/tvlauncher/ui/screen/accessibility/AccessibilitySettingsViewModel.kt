package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nl.ndat.tvlauncher.util.AccessibilityPreferences
import nl.ndat.tvlauncher.util.FocusRestoreMode

class AccessibilitySettingsViewModel(
	private val preferences: AccessibilityPreferences,
) : ViewModel() {
	var focusRestoreMode by mutableStateOf(preferences.focusRestoreMode())
		private set

	fun selectFocusRestoreMode(mode: FocusRestoreMode) {
		preferences.setFocusRestoreMode(mode)
		focusRestoreMode = mode
	}
}
