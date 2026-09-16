package nl.ndat.tvlauncher.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RefreshStalenessTrackerTest {
	@Test
	fun `remains stale until a refresh succeeds`() {
		val tracker = RefreshStalenessTracker(staleAfterMs = 60_000L)

		assertTrue(tracker.isStale(nowElapsed = 60_000L))
		assertTrue(tracker.isStale(nowElapsed = 60_001L))

		tracker.markSuccessfulRefresh(nowElapsed = 60_001L)

		assertFalse(tracker.isStale(nowElapsed = 120_000L))
		assertTrue(tracker.isStale(nowElapsed = 120_001L))
	}
}
