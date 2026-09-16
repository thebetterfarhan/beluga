package nl.ndat.tvlauncher.util

class LauncherStateRecorder(private val store: LauncherStateStore) {
	private var state: LauncherStateSnapshot = store.get()
	private var persisted: LauncherStateSnapshot = store.get()

	val snapshot: LauncherStateSnapshot get() = state

	fun recordDestination(destination: SavedDestination) {
		if (state.destination == destination) return
		state = state.copy(destination = destination)
	}

	fun recordFocusedItem(itemId: String?) {
		if (state.focusedItemId == itemId) return
		state = state.copy(focusedItemId = itemId)
	}

	fun recordScrollOffset(key: String, firstVisibleItemIndex: Int) {
		if (state.scrollOffsets[key] == firstVisibleItemIndex) return
		state = state.copy(scrollOffsets = state.scrollOffsets + (key to firstVisibleItemIndex))
	}

	fun scrollOffset(key: String): Int = state.scrollOffsets[key] ?: 0

	fun flush() {
		if (persisted == state) return
		store.set(state)
		persisted = state
	}
}
