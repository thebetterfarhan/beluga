package nl.ndat.tvlauncher.util

/** Tracks the time of successful refreshes without treating failed work as fresh. */
class RefreshStalenessTracker(private val staleAfterMs: Long) {
	private var lastSuccessfulRefreshElapsed = 0L

	fun isStale(nowElapsed: Long): Boolean = nowElapsed - lastSuccessfulRefreshElapsed >= staleAfterMs

	fun markSuccessfulRefresh(nowElapsed: Long) {
		lastSuccessfulRefreshElapsed = nowElapsed
	}
}
