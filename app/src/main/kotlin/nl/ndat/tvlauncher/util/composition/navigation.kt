package nl.ndat.tvlauncher.util.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import nl.ndat.tvlauncher.data.DefaultDestination
import nl.ndat.tvlauncher.data.Destination
import nl.ndat.tvlauncher.data.Destinations

val LocalBackStack = staticCompositionLocalOf<SnapshotStateList<Destination>> {
	error("LocalBackStack not provided")
}

// Backstack survives config changes via a class-name Saver.
internal val DestinationListSaver: Saver<SnapshotStateList<Destination>, Any> =
	Saver(
		save = { stack: SnapshotStateList<Destination> ->
			ArrayList(stack.map { it::class.java.name })
		},
		restore = { saved: Any ->
			@Suppress("UNCHECKED_CAST")
			val names = saved as ArrayList<String>
			mutableStateListOf<Destination>().apply {
				for (name in names) {
					val destination: Destination = when (name) {
						Destinations.Home::class.java.name -> Destinations.Home
						Destinations.Apps::class.java.name -> Destinations.Apps
						Destinations.Accessibility::class.java.name -> Destinations.Accessibility
						Destinations.FocusRestoration::class.java.name -> Destinations.FocusRestoration
						Destinations.OrientationHelp::class.java.name -> Destinations.OrientationHelp
						else -> return@apply
					}
					add(destination)
				}
			}
		},
	)

@Composable
fun ProvideNavigation(
	backStack: SnapshotStateList<Destination> = rememberSaveable(saver = DestinationListSaver) {
		mutableStateListOf<Destination>(DefaultDestination)
	},
	content: @Composable () -> Unit,
) = CompositionLocalProvider(
	LocalBackStack provides backStack,
	content = content,
)
