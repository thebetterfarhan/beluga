package nl.ndat.tvlauncher.ui.screen.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import nl.ndat.tvlauncher.data.Destination
import nl.ndat.tvlauncher.ui.toolbar.Toolbar
import nl.ndat.tvlauncher.util.composition.DestinationListSaver
import nl.ndat.tvlauncher.util.composition.LocalBackStack
import nl.ndat.tvlauncher.util.composition.LocalStartupFocus
import nl.ndat.tvlauncher.util.composition.ProvideNavigation
import nl.ndat.tvlauncher.util.modifier.debugDpadLog
import org.koin.androidx.compose.koinViewModel

@Composable
fun LauncherScreen() {
		val viewModel = koinViewModel<LauncherScreenViewModel>()
		val startupFocusRequester = remember { FocusRequester() }
		var startupFocusPending by rememberSaveable { mutableStateOf(true) }
		val backStack = rememberSaveable(saver = DestinationListSaver) {
			mutableStateListOf<Destination>(viewModel.startupDestination)
		}

	LaunchedEffect(backStack.lastOrNull()) {
		backStack.lastOrNull()?.let(viewModel::rememberDestination)
	}

	ProvideNavigation(backStack) {
		LocalStartupFocus(
			requester = startupFocusRequester,
			pending = startupFocusPending,
			target = viewModel.startupFocusTarget,
			onFocused = { startupFocusPending = false },
			itemId = viewModel.startupItemId,
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.onPreviewKeyEvent { event ->
						if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.repeatCount == 0 && when (event.key) {
							Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft,
							Key.DirectionRight, Key.DirectionCenter -> true
							else -> false
						}) debugDpadLog(event.nativeKeyEvent.keyCode)
						false
					}
			) {
				Toolbar(
					modifier = Modifier
						.padding(
							vertical = 27.dp,
							horizontal = 48.dp,
						)
				)

				Box {
				val backStack = LocalBackStack.current
				BackHandler(enabled = backStack.size > 1) {
					backStack.removeAt(backStack.lastIndex)
				}
					// Keep only the visible destination in the accessibility tree. NavDisplay
					// can retain the previous Home entry during a destination change.
					backStack.lastOrNull()?.let { destination ->
						key(destination) { destination.Content() }
					}
				}
			}
		}
	}
}
