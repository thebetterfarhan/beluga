package nl.ndat.tvlauncher.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherStateRecorderTest {
	@Test
	fun `recording does not touch the store until flush`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val recorder = LauncherStateRecorder(store)

		recorder.recordDestination(SavedDestination.APPS)
		recorder.recordFocusedItem("com.example.app")

		assertEquals(SavedDestination.HOME, store.get().destination)
		assertNull(store.get().focusedItemId)
	}

	@Test
	fun `flush persists recorded state`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val recorder = LauncherStateRecorder(store)

		recorder.recordDestination(SavedDestination.APPS)
		recorder.recordFocusedItem("com.example.app")
		recorder.flush()

		val persisted = store.get()
		assertEquals(SavedDestination.APPS, persisted.destination)
		assertEquals("com.example.app", persisted.focusedItemId)
	}

	@Test
	fun `recording the same value repeatedly keeps only the newest change`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val recorder = LauncherStateRecorder(store)

		recorder.recordFocusedItem("a")
		recorder.recordFocusedItem("a")
		recorder.recordFocusedItem("b")
		recorder.flush()

		assertEquals("b", store.get().focusedItemId)
	}

	@Test
	fun `flush without changes does not rewrite the store`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val expected = store.get()
		val recorder = LauncherStateRecorder(store)

		recorder.flush()

		assertEquals(expected, store.get())
	}

	@Test
	fun `scroll offsets are recorded in memory and persist on flush`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val recorder = LauncherStateRecorder(store)

		recorder.recordScrollOffset(LauncherStateScrollPositions.HOME_TAB, 7)
		assertTrue(store.get().scrollOffsets.isEmpty())

		recorder.flush()

		assertEquals(7, store.get().scrollOffsets[LauncherStateScrollPositions.HOME_TAB])
	}

	@Test
	fun `scroll offsets overwrite per key and default to 0 for unknown keys`() {
		val store = LauncherStateStore(InMemoryPreferenceStore())
		val recorder = LauncherStateRecorder(store)

		assertEquals(0, recorder.scrollOffset("unknown"))

		recorder.recordScrollOffset(LauncherStateScrollPositions.APPS_TAB, 4)
		recorder.recordScrollOffset(LauncherStateScrollPositions.APPS_TAB, 9)

		assertEquals(0, recorder.scrollOffset(LauncherStateScrollPositions.HOME_TAB))
		assertEquals(9, recorder.scrollOffset(LauncherStateScrollPositions.APPS_TAB))
	}
}
