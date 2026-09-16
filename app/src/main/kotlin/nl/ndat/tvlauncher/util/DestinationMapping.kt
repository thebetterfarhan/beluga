package nl.ndat.tvlauncher.util

import nl.ndat.tvlauncher.data.Destination
import nl.ndat.tvlauncher.data.Destinations

fun Destination.toSavedDestination(): SavedDestination = when (this) {
	Destinations.Home -> SavedDestination.HOME
	Destinations.Apps -> SavedDestination.APPS
	Destinations.Accessibility -> SavedDestination.ACCESSIBILITY
	Destinations.FocusRestoration -> SavedDestination.FOCUS_RESTORATION
	Destinations.OrientationHelp -> SavedDestination.ORIENTATION_HELP
	else -> SavedDestination.HOME
}

fun SavedDestination.toDestination(): Destination = when (this) {
	SavedDestination.HOME -> Destinations.Home
	SavedDestination.APPS -> Destinations.Apps
	SavedDestination.ACCESSIBILITY -> Destinations.Accessibility
	SavedDestination.FOCUS_RESTORATION -> Destinations.FocusRestoration
	SavedDestination.ORIENTATION_HELP -> Destinations.OrientationHelp
}

fun SavedDestination.isTabDestination(): Boolean =
	this == SavedDestination.HOME || this == SavedDestination.APPS

