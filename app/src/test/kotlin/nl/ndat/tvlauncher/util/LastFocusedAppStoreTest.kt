package nl.ndat.tvlauncher.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LastFocusedAppStoreTest {
	@Test
	fun `get returns null when nothing is stored`() {
		val store = LastFocusedAppStore(InMemoryPreferenceStore())
		assertNull(store.get())
	}

	@Test
	fun `set stores the app id`() {
		val store = LastFocusedAppStore(InMemoryPreferenceStore())
		store.set("com.example.app")
		assertEquals("com.example.app", store.get())
	}

	@Test
	fun `set overwrites a previous value`() {
		val store = LastFocusedAppStore(InMemoryPreferenceStore())
		store.set("first")
		store.set("second")
		assertEquals("second", store.get())
	}
}
