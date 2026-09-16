package nl.ndat.tvlauncher.ui.component.card

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.sqldelight.App
import nl.ndat.tvlauncher.ui.component.PopupContainer
import nl.ndat.tvlauncher.util.createDrawable
import nl.ndat.tvlauncher.util.debugTrace
import nl.ndat.tvlauncher.util.modifier.debugFocusLog
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import nl.ndat.tvlauncher.util.modifier.debugFocusRequestLog
import nl.ndat.tvlauncher.util.modifier.ifElse

@Composable
fun AppCard(
	app: App,
	modifier: Modifier = Modifier,
	baseHeight: Dp = 90.dp,
	onFocused: (() -> Unit)? = null,
	popupContent: (@Composable (firstActionModifier: Modifier, onAction: () -> Unit) -> Unit)? = null,
) {
	val context = LocalContext.current
	val image by produceState<Drawable?>(
		initialValue = null,
		app.id,
		app.launchIntentUriDefault,
		app.launchIntentUriLeanback,
	) {
		value = withContext(Dispatchers.IO) { app.createDrawable(context) }
	}
	val interactionSource = remember { MutableInteractionSource() }
	val focused by interactionSource.collectIsFocusedAsState()
	val cardFocusRequester = remember { FocusRequester() }

	val launchIntentUri = app.launchIntentUriLeanback ?: app.launchIntentUriDefault
	val appOptionsLabel = stringResource(R.string.app_options)

	var menuVisible by remember { mutableStateOf(false) }
	var restoreFocusAfterMenuDismissal by remember { mutableStateOf(false) }
	val dismissMenu = {
		restoreFocusAfterMenuDismissal = true
		menuVisible = false
	}

	PopupContainer(
		visible = menuVisible && popupContent != null,
		onDismiss = {
			dismissMenu()
		},
		content = {
			StandardCardContainer(
				modifier = modifier
					.width(baseHeight * (16f / 9f)),
				interactionSource = interactionSource,
				title = {
					Text(
						text = app.displayName,
						maxLines = 1,
						overflow = TextOverflow.Clip,
						softWrap = false,
						style = MaterialTheme.typography.bodyMedium.copy(
							fontWeight = FontWeight.SemiBold
						),
						modifier = Modifier
							// The Card below is the single actionable accessibility node.
							.clearAndSetSemantics { }
							.ifElse(
								focused,
								Modifier.basicMarquee(
									iterations = Int.MAX_VALUE,
									initialDelayMillis = 0,
								),
							)
							.padding(top = 6.dp),
					)
				},
				imageCard = { _ ->
					Card(
						modifier = Modifier
							.height(baseHeight)
							.aspectRatio(16f / 9f)
							.focusRequester(cardFocusRequester)
							.onFocusChanged { if (it.isFocused) debugTrace("focus-write:app-${app.id}") { onFocused?.invoke() } }
							.debugFocusLog("app-card:${app.id}")
						.semantics {
								contentDescription = app.displayName
								role = Role.Button
								if (popupContent != null) {
									onLongClick(label = appOptionsLabel) {
										menuVisible = true
										true
									}
								}
							},
						interactionSource = interactionSource,
						border = CardDefaults.border(
							focusedBorder = Border(
								border = BorderStroke(2.dp, MaterialTheme.colorScheme.border),
							)
						),
						scale = CardDefaults.scale(focusedScale = 1f),
onClick = {
							val launchUri = launchIntentUri
							if (launchUri != null) {
								try {
									context.startActivity(Intent.parseUri(launchUri, 0))
								} catch (err: Throwable) {
									debugLauncherLog("app-card:${app.id}: launch failed: ${err.message}")
								}
							}
						},
						onLongClick = {
							if (popupContent != null) {
								menuVisible = true
							}
						}
					) {
						if (image != null) {
							AsyncImage(
								modifier = Modifier.fillMaxSize(),
								model = image,
								// The card title supplies this tile's single accessible name.
								// Describing the decorative artwork as well makes TalkBack
								// announce the app twice for one focused card.
								contentDescription = null,
							)
						}
					}
				}
			)
		},
		popupContent = { firstActionModifier ->
			if (popupContent != null) popupContent(firstActionModifier, dismissMenu)
		}
	)

	// Wait until the Popup window is gone before restoring the invoking tile.
	LaunchedEffect(restoreFocusAfterMenuDismissal) {
		if (restoreFocusAfterMenuDismissal) {
			debugFocusRequestLog("app-card:${app.id}:popup-dismiss")
			cardFocusRequester.requestFocus()
			restoreFocusAfterMenuDismissal = false
		}
	}
}
