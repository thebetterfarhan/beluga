package nl.ndat.tvlauncher.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherStateStoreTest {
	@Test
	fun `get returns default snapshot when nothing is stored`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val snapshot = store.get()

		assertEquals(CurrentLauncherStateVersion, snapshot.version)
		assertEquals(SavedDestination.HOME, snapshot.destination)
		assertNull(snapshot.focusedItemId)
		assertTrue(snapshot.scrollOffsets.isEmpty())
		assertTrue(snapshot.expandedSectionIds.isEmpty())
	}

	@Test
	fun `set and get round-trips a snapshot`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val expected = LauncherStateSnapshot(
			destination = SavedDestination.APPS,
			focusedItemId = "com.example.app",
			scrollOffsets = mapOf("home" to 42),
			expandedSectionIds = setOf("watch_next"),
		)

		store.set(expected)
		val actual = store.get()

		assertEquals(expected, actual)
	}

	@Test
	fun `corrupt JSON returns default snapshot`() {
		val backing = InMemoryPreferenceStore()
		backing.setString("snapshot", "not valid json {{{")

		val store = LauncherStateStore(backing)
		val snapshot = store.get()

		assertEquals(SavedDestination.HOME, snapshot.destination)
		assertNull(snapshot.focusedItemId)
	}

	@Test
	fun `clear removes stored snapshot`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		store.set(LauncherStateSnapshot(destination = SavedDestination.ACCESSIBILITY))
		store.clear()

		val snapshot = store.get()
		assertEquals(SavedDestination.HOME, snapshot.destination)
	}
}
