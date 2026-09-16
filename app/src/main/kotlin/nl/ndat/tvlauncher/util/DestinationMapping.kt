package nl.ndat.tvlauncher.util

import nl.ndat.tvlauncher.data.Destination
import nl.ndat.tvlauncher.data.Destinations

fun Destination.toSavedDestination(): SavedDestination = when (this) {
	Destinations.Home -> SavedDestination.HOME
	Destinations.Apps -> SavedDestination.APPS
	Destinations.Accessibility -> SavedDestination.ACCESSIBILITY
	Destinations.FocusRestoration -> SavedDestination.FOCUS_RESTORATION
	Destinations.OrientationHelp -> SavedDestination.ORIENTATION_HELP
	Destinations.HiddenApps -> SavedDestination.HIDDEN_APPS
	Destinations.HomePreferences -> SavedDestination.HOME_PREFERENCES
	Destinations.AppLanguage -> SavedDestination.APP_LANGUAGE
	Destinations.ChannelPreferences -> SavedDestination.CHANNEL_PREFERENCES
	else -> SavedDestination.HOME
}

fun SavedDestination.toDestination(): Destination = when (this) {
	SavedDestination.HOME -> Destinations.Home
	SavedDestination.APPS -> Destinations.Apps
	SavedDestination.ACCESSIBILITY -> Destinations.Accessibility
	SavedDestination.FOCUS_RESTORATION -> Destinations.FocusRestoration
	SavedDestination.ORIENTATION_HELP -> Destinations.OrientationHelp
	SavedDestination.HIDDEN_APPS -> Destinations.HiddenApps
	SavedDestination.HOME_PREFERENCES -> Destinations.HomePreferences
	SavedDestination.APP_LANGUAGE -> Destinations.AppLanguage
	SavedDestination.CHANNEL_PREFERENCES -> Destinations.ChannelPreferences
}

fun SavedDestination.isTabDestination(): Boolean =
	this == SavedDestination.HOME || this == SavedDestination.APPS

