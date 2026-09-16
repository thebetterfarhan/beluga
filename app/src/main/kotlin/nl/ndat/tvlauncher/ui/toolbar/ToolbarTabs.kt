package nl.ndat.tvlauncher.ui.toolbar

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.Destinations
import nl.ndat.tvlauncher.util.composition.LocalBackStack
import nl.ndat.tvlauncher.util.composition.LocalStartupFocus
import nl.ndat.tvlauncher.util.StartupFocusTarget
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import nl.ndat.tvlauncher.util.modifier.debugFocusLog

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ToolbarTabs(
	modifier: Modifier,
) {
	val backStack = LocalBackStack.current
	val startupFocus = LocalStartupFocus
	val currentDestination = backStack.lastOrNull()
	val tabs = mapOf(
		Destinations.Home to stringResource(R.string.tab_home),
		Destinations.Apps to stringResource(R.string.tab_apps),
	)
	if (currentDestination !in tabs) return

	TabRow(
		selectedTabIndex = tabs.keys.indexOfFirst { destination -> destination == currentDestination },
		modifier = modifier.focusRestorer(),
	) {
			tabs.toList().forEachIndexed { index, (destination, name) ->
			key(index) {
				Tab(
					selected = destination == currentDestination,
					onClick = {
						if (destination == currentDestination) return@Tab
						// Home is the root entry. Replacing Apps with Home must not append
						// a second Home, otherwise Back appears to do nothing.
						if (backStack.lastOrNull() != Destinations.Home) {
							backStack.removeAt(backStack.lastIndex)
						}
						if (destination != Destinations.Home) backStack.add(destination)
						debugLauncherLog("tab changed: $currentDestination -> $destination")
					},
					onFocus = {},
					modifier = Modifier
						.then(
							if (destination == Destinations.Home && startupFocus?.target == StartupFocusTarget.HOME_TAB) {
								Modifier.focusRequester(startupFocus.requester)
							} else Modifier
						)
						.debugFocusLog("tab:$name")
						.padding(16.dp, 8.dp)
				) {
					Text(name)
				}
			}
		}
	}

	LaunchedEffect(startupFocus?.pending, startupFocus?.target, currentDestination) {
		if (startupFocus?.pending == true && startupFocus.target == StartupFocusTarget.HOME_TAB && currentDestination == Destinations.Home) {
			startupFocus.requester.requestFocus()
			startupFocus.onFocused()
		}
	}
}
