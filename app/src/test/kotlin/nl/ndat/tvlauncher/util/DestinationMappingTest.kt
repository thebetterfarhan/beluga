package nl.ndat.tvlauncher.util

import nl.ndat.tvlauncher.data.Destinations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DestinationMappingTest {
	@Test
	fun `Destination to SavedDestination round trips for all values`() {
		val pairs = listOf(
			Destinations.Home to SavedDestination.HOME,
			Destinations.Apps to SavedDestination.APPS,
			Destinations.Accessibility to SavedDestination.ACCESSIBILITY,
			Destinations.FocusRestoration to SavedDestination.FOCUS_RESTORATION,
			Destinations.OrientationHelp to SavedDestination.ORIENTATION_HELP,
			Destinations.HiddenApps to SavedDestination.HIDDEN_APPS,
			Destinations.HomePreferences to SavedDestination.HOME_PREFERENCES,
			Destinations.AppLanguage to SavedDestination.APP_LANGUAGE,
		)

		pairs.forEach { (destination, saved) ->
			assertEquals(saved, destination.toSavedDestination())
			assertEquals(destination, saved.toDestination())
		}
	}

	@Test
	fun `only Home and Apps are tab destinations`() {
		assertTrue(SavedDestination.HOME.isTabDestination())
		assertTrue(SavedDestination.APPS.isTabDestination())
		assertFalse(SavedDestination.ACCESSIBILITY.isTabDestination())
		assertFalse(SavedDestination.FOCUS_RESTORATION.isTabDestination())
		assertFalse(SavedDestination.ORIENTATION_HELP.isTabDestination())
		assertFalse(SavedDestination.HIDDEN_APPS.isTabDestination())
		assertFalse(SavedDestination.HOME_PREFERENCES.isTabDestination())
		assertFalse(SavedDestination.APP_LANGUAGE.isTabDestination())
	}
}
