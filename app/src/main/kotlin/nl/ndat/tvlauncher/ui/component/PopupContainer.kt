package nl.ndat.tvlauncher.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import nl.ndat.tvlauncher.util.modifier.debugFocusLog
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import nl.ndat.tvlauncher.util.modifier.debugFocusRequestLog
import androidx.tv.material3.Border
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import nl.ndat.tvlauncher.R

private class AlignedPositionProvider : PopupPositionProvider {
	override fun calculatePosition(
		anchorBounds: IntRect,
		windowSize: IntSize,
		layoutDirection: LayoutDirection,
		popupContentSize: IntSize
	): IntOffset {
		val anchorAlignmentPoint = Alignment.BottomCenter.align(
			IntSize.Zero,
			anchorBounds.size,
			layoutDirection
		)

		val popupAlignmentPoint = -Alignment.BottomCenter.align(
			IntSize.Zero,
			IntSize(popupContentSize.width, 0),
			layoutDirection
		)

		return anchorBounds.topLeft + anchorAlignmentPoint + popupAlignmentPoint
	}
}

@Composable
fun PopupContainer(
	visible: Boolean,
	onDismiss: () -> Unit,
	content: @Composable () -> Unit,
	popupContent: @Composable (firstActionModifier: Modifier) -> Unit,
) {
	val firstActionFocusRequester = remember { FocusRequester() }
	val popupTitle = stringResource(R.string.app_options)

	Box {
		content()

		if (visible) {
			Popup(
				popupPositionProvider = AlignedPositionProvider(),
				onDismissRequest = onDismiss,
				properties = PopupProperties(
					focusable = true,
					dismissOnBackPress = true,
					dismissOnClickOutside = true,
				)
			) {
				Surface(
					modifier = Modifier.semantics {
						paneTitle = popupTitle
					},
					colors = SurfaceDefaults.colors(
						containerColor = MaterialTheme.colorScheme.secondaryContainer,
						contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
					),
					shape = RoundedCornerShape(8.dp),
					border = Border(
						border = BorderStroke(2.dp, Color.White),
						shape = RoundedCornerShape(8.dp),
					),
				) {
					Box(modifier = Modifier.padding(8.dp).focusRestorer(firstActionFocusRequester)) {
						popupContent(
							Modifier
								.focusRequester(firstActionFocusRequester)
								.debugFocusLog("popup-first-action")
						)
					}
				}
			}
		}
	}

	// A focusable Popup owns a separate window. Explicitly put D-pad focus on
	// its first action instead of leaving the user on the now-obscured card.
	LaunchedEffect(visible) {
		if (visible) {
			debugLauncherLog("popup: opened")
			debugFocusRequestLog("popup-first-action")
			firstActionFocusRequester.requestFocus()
		}
	}
}
