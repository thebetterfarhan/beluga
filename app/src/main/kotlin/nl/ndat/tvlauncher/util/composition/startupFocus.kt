package nl.ndat.tvlauncher.util.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.focus.FocusRequester
import nl.ndat.tvlauncher.util.StartupFocusTarget

data class StartupFocus(
	val requester: FocusRequester,
	val pending: Boolean,
	val target: StartupFocusTarget,
	val onFocused: () -> Unit,
	val itemId: String? = null,
)

private val LocalStartupFocusState = staticCompositionLocalOf<StartupFocus?> { null }

val LocalStartupFocus: StartupFocus?
	@Composable get() = LocalStartupFocusState.current

@Composable
fun LocalStartupFocus(
	requester: FocusRequester,
	pending: Boolean,
	target: StartupFocusTarget,
	onFocused: () -> Unit,
	itemId: String? = null,
	content: @Composable () -> Unit,
) = CompositionLocalProvider(
	LocalStartupFocusState provides StartupFocus(requester, pending, target, onFocused, itemId),
	content = content,
)
