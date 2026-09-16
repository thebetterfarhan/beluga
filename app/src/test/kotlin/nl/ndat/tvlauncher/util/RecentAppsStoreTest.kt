package nl.ndat.tvlauncher.util

import org.junit.Assert.assertEquals
import org.junit.Test

class RecentAppsStoreTest {
	@Test
	fun emptyStoreReturnsEmptyList() {
		val store = RecentAppsStore(InMemoryPreferenceStore())
		assertEquals(emptyList<String>(), store.get())
	}

	@Test
	fun preservesMostRecentFirst() {
		val store = RecentAppsStore(InMemoryPreferenceStore())
		store.add("app:a")
		store.add("app:b")
		store.add("app:c")
		assertEquals(listOf("app:c", "app:b", "app:a"), store.get())
	}

	@Test
	fun repeatedIdMovesToHead() {
		val store = RecentAppsStore(InMemoryPreferenceStore())
		store.add("app:a")
		store.add("app:b")
		store.add("app:c")
		store.add("app:a")
		assertEquals(listOf("app:a", "app:c", "app:b"), store.get())
	}

	@Test
	fun capsAtStoreCap() {
		val store = RecentAppsStore(InMemoryPreferenceStore())
		repeat(RecentAppsStore.MRU_CAP + 5) { index ->
			store.add("app:$index")
		}
		val all = store.get()
		assertEquals(RecentAppsStore.MRU_CAP, all.size)
		// most recent first: the last inserted item is at index 0.
		assertEquals("app:${RecentAppsStore.MRU_CAP + 4}", all.first())
	}

	@Test
	fun emptyIdIgnored() {
		val store = RecentAppsStore(InMemoryPreferenceStore())
		store.add("")
		store.add("app:a")
		store.add("")
		assertEquals(listOf("app:a"), store.get())
	}

	@Test
	fun clearEmptiesList() {
		val store = RecentAppsStore(InMemoryPreferenceStore())
		store.add("app:a")
		store.add("app:b")
		store.clear()
		assertEquals(emptyList<String>(), store.get())
	}

	@Test
	fun survivesRoundTripThroughPreferenceStore() {
		val prefs = InMemoryPreferenceStore()
		val writer = RecentAppsStore(prefs)
		writer.add("app:a")
		writer.add("app:b")
		val reader = RecentAppsStore(prefs)
		assertEquals(listOf("app:b", "app:a"), reader.get())
	}
}
