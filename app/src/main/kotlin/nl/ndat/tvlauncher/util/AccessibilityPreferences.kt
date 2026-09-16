package nl.ndat.tvlauncher.util

import android.content.Context

class AccessibilityPreferences(private val store: PreferenceStore) : FocusRestoreModeProvider {
	constructor(context: Context) : this(SharedPreferenceStore(context, "accessibility_preferences"))

	override fun focusRestoreMode(): FocusRestoreMode {
		store.getString("focus_restore_mode", null)?.let { saved ->
			return runCatching { FocusRestoreMode.valueOf(saved) }.getOrElse {
				FocusRestoreMode.LAST_FOCUSED_APP.also(::setFocusRestoreMode)
			}
		}
		val migrated = if (store.getBoolean("restore_last_focused_app", true)) {
			FocusRestoreMode.LAST_FOCUSED_APP
		} else FocusRestoreMode.FIRST_FAVORITE
		setFocusRestoreMode(migrated)
		store.remove("restore_last_focused_app")
		return migrated
	}

	fun setFocusRestoreMode(mode: FocusRestoreMode) = store.setString("focus_restore_mode", mode.name)
}
