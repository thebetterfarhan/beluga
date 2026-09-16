package nl.ndat.tvlauncher.util

import nl.ndat.tvlauncher.data.sqldelight.App
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusRestorationManagerTest {
	private fun manager(mode: FocusRestoreMode) = FocusRestorationManager { mode }

	private fun app(id: String, favoriteOrder: Long? = null) = App(
		id = id,
		displayName = "App $id",
		packageName = "com.example.$id",
		launchIntentUriDefault = null,
		launchIntentUriLeanback = null,
		favoriteOrder = favoriteOrder,
	)

	@Test
	fun `startup focus target maps modes correctly`() {
		assertEquals(StartupFocusTarget.FAVORITE, manager(FocusRestoreMode.LAST_FOCUSED_APP).startupFocusTarget())
		assertEquals(StartupFocusTarget.FAVORITE, manager(FocusRestoreMode.FIRST_FAVORITE).startupFocusTarget())
		assertEquals(StartupFocusTarget.HOME_TAB, manager(FocusRestoreMode.HOME_TAB).startupFocusTarget())
		assertEquals(StartupFocusTarget.ALL_APPS_TAB, manager(FocusRestoreMode.ALL_APPS_TAB).startupFocusTarget())
		assertEquals(
			StartupFocusTarget.FAVORITE,
			manager(FocusRestoreMode.COMPLETE_LAUNCHER_STATE).startupFocusTarget(),
		)
	}

	@Test
	fun `LAST_FOCUSED_APP returns matching index when app is present`() {
		val apps = listOf(app("a"), app("b"), app("c"))
		assertEquals(2, manager(FocusRestoreMode.LAST_FOCUSED_APP).preferredFavoriteIndex(apps, "c"))
	}

	@Test
	fun `LAST_FOCUSED_APP falls back to first app when stored id is unknown`() {
		val apps = listOf(app("a"), app("b"))
		assertEquals(0, manager(FocusRestoreMode.LAST_FOCUSED_APP).preferredFavoriteIndex(apps, "missing"))
	}

	@Test
	fun `LAST_FOCUSED_APP falls back to zero when list is empty`() {
		assertEquals(0, manager(FocusRestoreMode.LAST_FOCUSED_APP).preferredFavoriteIndex(emptyList(), "a"))
	}

	@Test
	fun `LAST_FOCUSED_APP falls back to zero when stored id is null`() {
		assertEquals(0, manager(FocusRestoreMode.LAST_FOCUSED_APP).preferredFavoriteIndex(listOf(app("a")), null))
	}

	@Test
	fun `FIRST_FAVORITE always prefers the first app`() {
		val apps = listOf(app("a"), app("b"), app("c"))
		assertEquals(0, manager(FocusRestoreMode.FIRST_FAVORITE).preferredFavoriteIndex(apps, "c"))
	}

	@Test
	fun `HOME_TAB and ALL_APPS_TAB prefer the first app`() {
		val apps = listOf(app("a"), app("b"))
		assertEquals(0, manager(FocusRestoreMode.HOME_TAB).preferredFavoriteIndex(apps, "b"))
		assertEquals(0, manager(FocusRestoreMode.ALL_APPS_TAB).preferredFavoriteIndex(apps, "b"))
	}

	@Test
	fun `COMPLETE_LAUNCHER_STATE uses honest fallback to first app`() {
		val apps = listOf(app("a"), app("b"))
		assertEquals(0, manager(FocusRestoreMode.COMPLETE_LAUNCHER_STATE).preferredFavoriteIndex(apps, "b"))
	}

	@Test
	fun `only COMPLETE_LAUNCHER_STATE restores launcher state`() {
		assertTrue(manager(FocusRestoreMode.COMPLETE_LAUNCHER_STATE).restoresLauncherState())
		assertFalse(manager(FocusRestoreMode.LAST_FOCUSED_APP).restoresLauncherState())
		assertFalse(manager(FocusRestoreMode.FIRST_FAVORITE).restoresLauncherState())
		assertFalse(manager(FocusRestoreMode.HOME_TAB).restoresLauncherState())
		assertFalse(manager(FocusRestoreMode.ALL_APPS_TAB).restoresLauncherState())
	}
}
