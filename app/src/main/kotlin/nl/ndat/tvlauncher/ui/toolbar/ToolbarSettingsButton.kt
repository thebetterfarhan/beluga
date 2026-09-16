package nl.ndat.tvlauncher.ui.toolbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.Destinations
import nl.ndat.tvlauncher.util.composition.LocalBackStack
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import nl.ndat.tvlauncher.util.modifier.debugFocusLog

@Composable
fun ToolbarSettingsButton() = Box {
	val backStack = LocalBackStack.current

	IconButton(
		onClick = {
			if (backStack.lastOrNull() == Destinations.Accessibility) return@IconButton
			debugLauncherLog("settings: opened")
			backStack.add(Destinations.Accessibility)
		},
		modifier = androidx.compose.ui.Modifier.debugFocusLog("settings"),
	) {
		Icon(
			imageVector = Icons.Outlined.Settings,
			contentDescription = stringResource(id = R.string.settings),
		)
	}
}
