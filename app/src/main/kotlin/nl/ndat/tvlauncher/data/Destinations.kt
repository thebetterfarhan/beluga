package nl.ndat.tvlauncher.data

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import nl.ndat.tvlauncher.ui.tab.apps.AppsTab
import nl.ndat.tvlauncher.ui.tab.home.HomeTab
import nl.ndat.tvlauncher.ui.screen.accessibility.AccessibilityOverviewScreen
import nl.ndat.tvlauncher.ui.screen.accessibility.FocusRestorationScreen
import nl.ndat.tvlauncher.ui.screen.accessibility.HiddenAppsScreen
import nl.ndat.tvlauncher.ui.screen.accessibility.OrientationHelpScreen

interface Destination {
	@Composable
	fun Content()
}

object Destinations {
	@Serializable
	object Home : Destination {
		@Composable
		override fun Content() = HomeTab()
	}

	@Serializable
	object Apps : Destination {
		@Composable
		override fun Content() = AppsTab()
	}

	@Serializable
	object Accessibility : Destination {
		@Composable
		override fun Content() = AccessibilityOverviewScreen()
	}

	@Serializable
	object FocusRestoration : Destination {
		@Composable
		override fun Content() = FocusRestorationScreen()
	}

	@Serializable
	object OrientationHelp : Destination {
		@Composable
		override fun Content() = OrientationHelpScreen()
	}

	@Serializable
	object HiddenApps : Destination {
		@Composable
		override fun Content() = HiddenAppsScreen()
	}
}

val DefaultDestination = Destinations.Home
