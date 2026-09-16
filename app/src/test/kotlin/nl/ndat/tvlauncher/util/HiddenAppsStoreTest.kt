package nl.ndat.tvlauncher.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HiddenAppsStoreTest {
	@Test
	fun emptyStoreReturnsEmptySet() {
		val store = HiddenAppsStore(InMemoryPreferenceStore())
		assertEquals(emptySet<String>(), store.get())
	}

	@Test
	fun hideAddsAppId() {
		val store = HiddenAppsStore(InMemoryPreferenceStore())
		store.hide("app:a")
		assertTrue(store.isHidden("app:a"))
		assertFalse(store.isHidden("app:b"))
	}

	@Test
	fun unhideRemovesAppId() {
		val store = HiddenAppsStore(InMemoryPreferenceStore())
		store.hide("app:a")
		store.unhide("app:a")
		assertEquals(emptySet<String>(), store.get())
	}

	@Test
	fun unhideAllClearsStore() {
		val store = HiddenAppsStore(InMemoryPreferenceStore())
		store.hide("app:a")
		store.hide("app:b")
		store.unhideAll()
		assertEquals(emptySet<String>(), store.get())
	}

	@Test
	fun survivesRoundTripThroughPreferenceStore() {
		val prefs = InMemoryPreferenceStore()
		val writer = HiddenAppsStore(prefs)
		writer.hide("app:a")
		writer.hide("app:b")
		val reader = HiddenAppsStore(prefs)
		assertEquals(setOf("app:a", "app:b"), reader.get())
	}

	@Test
	fun emptyIdIgnored() {
		val store = HiddenAppsStore(InMemoryPreferenceStore())
		store.hide("")
		assertEquals(emptySet<String>(), store.get())
	}

	@Test
	fun duplicateHideIsIdempotent() {
		val store = HiddenAppsStore(InMemoryPreferenceStore())
		store.hide("app:a")
		store.hide("app:a")
		assertEquals(setOf("app:a"), store.get())
	}
}
