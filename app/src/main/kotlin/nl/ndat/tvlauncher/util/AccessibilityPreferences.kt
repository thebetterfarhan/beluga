package nl.ndat.tvlauncher.util

import android.content.Context

class AccessibilityPreferences(private val store: PreferenceStore) : FocusRestoreModeProvider {
	constructor(context: Context) : this(SharedPreferenceStore(context, "accessibility_preferences"))

	@Volatile
	private var cachedFocusRestoreMode: FocusRestoreMode? = null

	@Volatile
	private var cachedHasSeenOnboarding: Boolean? = null

	override fun focusRestoreMode(): FocusRestoreMode {
		cachedFocusRestoreMode?.let { return it }
		store.getString("focus_restore_mode", null)?.let { saved ->
			return runCatching { FocusRestoreMode.valueOf(saved) }.getOrElse {
				FocusRestoreMode.LAST_FOCUSED_APP.also(::setFocusRestoreMode)
			}.also { cachedFocusRestoreMode = it }
		}
		val migrated = if (store.getBoolean("restore_last_focused_app", true)) {
			FocusRestoreMode.LAST_FOCUSED_APP
		} else FocusRestoreMode.FIRST_FAVORITE
		setFocusRestoreMode(migrated)
		return migrated.also { cachedFocusRestoreMode = it }
	}

	fun setFocusRestoreMode(mode: FocusRestoreMode) {
		store.setString("focus_restore_mode", mode.name)
		cachedFocusRestoreMode = mode
	}

	fun hasSeenOnboarding(): Boolean {
		cachedHasSeenOnboarding?.let { return it }
		val result = store.getBoolean("has_seen_onboarding", false)
		cachedHasSeenOnboarding = result
		return result
	}

	fun setHasSeenOnboarding() {
		store.setBoolean("has_seen_onboarding", true)
		cachedHasSeenOnboarding = true
	}
}
