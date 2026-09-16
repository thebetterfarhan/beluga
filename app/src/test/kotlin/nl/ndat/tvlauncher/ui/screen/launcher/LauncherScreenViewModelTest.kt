package nl.ndat.tvlauncher.ui.screen.launcher

import nl.ndat.tvlauncher.data.Destinations
import nl.ndat.tvlauncher.util.FocusRestorationManager
import nl.ndat.tvlauncher.util.FocusRestoreMode
import nl.ndat.tvlauncher.util.InMemoryPreferenceStore
import nl.ndat.tvlauncher.util.LauncherStateRecorder
import nl.ndat.tvlauncher.util.LauncherStateSnapshot
import nl.ndat.tvlauncher.util.LauncherStateStore
import nl.ndat.tvlauncher.util.SavedDestination
import nl.ndat.tvlauncher.util.StartupFocusTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LauncherScreenViewModelTest {
	private inner class Fixture(
		mode: FocusRestoreMode,
		seed: LauncherStateSnapshot? = null,
	) {
		val backing = InMemoryPreferenceStore()
		val store = LauncherStateStore(backing).apply { seed?.let(::set) }
		val recorder = LauncherStateRecorder(store)
		val viewModel = LauncherScreenViewModel(FocusRestorationManager { mode }, recorder)
	}

	@Test
	fun `non-restoring mode always starts on Home`() {
		val fixture = Fixture(
			FocusRestoreMode.LAST_FOCUSED_APP,
			LauncherStateSnapshot(destination = SavedDestination.APPS, focusedItemId = "a"),
		)

		assertEquals(Destinations.Home, fixture.viewModel.startupDestination)
		assertNull(fixture.viewModel.startupItemId)
	}

	@Test
	fun `ALL_APPS_TAB mode starts on Apps`() {
		val fixture = Fixture(FocusRestoreMode.ALL_APPS_TAB)

		assertEquals(Destinations.Apps, fixture.viewModel.startupDestination)
		assertNull(fixture.viewModel.startupItemId)
	}

	@Test
	fun `COMPLETE_LAUNCHER_STATE restores a saved tab`() {
		val fixture = Fixture(
			FocusRestoreMode.COMPLETE_LAUNCHER_STATE,
			LauncherStateSnapshot(destination = SavedDestination.APPS),
		)

		assertEquals(Destinations.Apps, fixture.viewModel.startupDestination)
	}

	@Test
	fun `COMPLETE_LAUNCHER_STATE falls back to Home for a settings destination`() {
		val fixture = Fixture(
			FocusRestoreMode.COMPLETE_LAUNCHER_STATE,
			LauncherStateSnapshot(destination = SavedDestination.ACCESSIBILITY),
		)

		assertEquals(Destinations.Home, fixture.viewModel.startupDestination)
		assertNull(fixture.viewModel.startupItemId)
	}

	@Test
	fun `COMPLETE_LAUNCHER_STATE restores focus target with the saved tab`() {
		val fixture = Fixture(
			FocusRestoreMode.COMPLETE_LAUNCHER_STATE,
			LauncherStateSnapshot(
				destination = SavedDestination.APPS,
				focusedItemId = "com.example.app",
			),
		)

		assertEquals(Destinations.Apps, fixture.viewModel.startupDestination)
		assertEquals(StartupFocusTarget.ALL_APPS_TAB, fixture.viewModel.startupFocusTarget)
		assertEquals("com.example.app", fixture.viewModel.startupItemId)
	}

	@Test
	fun `COMPLETE_LAUNCHER_STATE keeps favorite focus when restoring Home`() {
		val fixture = Fixture(
			FocusRestoreMode.COMPLETE_LAUNCHER_STATE,
			LauncherStateSnapshot(destination = SavedDestination.HOME),
		)

		assertEquals(Destinations.Home, fixture.viewModel.startupDestination)
		assertEquals(StartupFocusTarget.FAVORITE, fixture.viewModel.startupFocusTarget)
	}

	@Test
	fun `rememberDestination persists tab destinations after flush`() {
		val fixture = Fixture(FocusRestoreMode.LAST_FOCUSED_APP)

		fixture.viewModel.rememberDestination(Destinations.Apps)
		fixture.recorder.flush()

		assertEquals(SavedDestination.APPS, LauncherStateStore(fixture.backing).get().destination)
	}

	@Test
	fun `rememberDestination ignores settings destinations`() {
		val fixture = Fixture(FocusRestoreMode.LAST_FOCUSED_APP)

		fixture.viewModel.rememberDestination(Destinations.Accessibility)
		fixture.recorder.flush()

		assertEquals(SavedDestination.HOME, LauncherStateStore(fixture.backing).get().destination)
	}
}
