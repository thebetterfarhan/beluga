package nl.ndat.tvlauncher.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AccessibilityPreferencesTest {
	@Test
	fun `default mode is LAST_FOCUSED_APP and persists`() {
		val prefs = AccessibilityPreferences(InMemoryPreferenceStore())
		assertEquals(FocusRestoreMode.LAST_FOCUSED_APP, prefs.focusRestoreMode())
		assertEquals(FocusRestoreMode.LAST_FOCUSED_APP, prefs.focusRestoreMode())
	}

	@Test
	fun `migration from true legacy flag selects LAST_FOCUSED_APP and removes flag`() {
		val store = InMemoryPreferenceStore()
		store.setBoolean("restore_last_focused_app", true)

		val prefs = AccessibilityPreferences(store)
		assertEquals(FocusRestoreMode.LAST_FOCUSED_APP, prefs.focusRestoreMode())
		assertFalse(store.contains("restore_last_focused_app"))
	}

	@Test
	fun `migration from false legacy flag selects FIRST_FAVORITE and removes flag`() {
		val store = InMemoryPreferenceStore()
		store.setBoolean("restore_last_focused_app", false)

		val prefs = AccessibilityPreferences(store)
		assertEquals(FocusRestoreMode.FIRST_FAVORITE, prefs.focusRestoreMode())
		assertFalse(store.contains("restore_last_focused_app"))
	}

	@Test
	fun `round trip persists each mode`() {
		FocusRestoreMode.entries.forEach { mode ->
			val prefs = AccessibilityPreferences(InMemoryPreferenceStore())
			prefs.setFocusRestoreMode(mode)
			assertEquals(mode, prefs.focusRestoreMode())
		}
	}

	@Test
	fun `invalid saved mode falls back to LAST_FOCUSED_APP and overwrites`() {
		val store = InMemoryPreferenceStore()
		store.setString("focus_restore_mode", "NOT_A_REAL_MODE")

		val prefs = AccessibilityPreferences(store)
		assertEquals(FocusRestoreMode.LAST_FOCUSED_APP, prefs.focusRestoreMode())
		assertEquals(FocusRestoreMode.LAST_FOCUSED_APP.name, store.getString("focus_restore_mode"))
	}
}
