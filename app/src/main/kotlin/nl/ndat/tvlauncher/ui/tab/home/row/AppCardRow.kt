package nl.ndat.tvlauncher.ui.tab.home.row

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.sqldelight.App
import nl.ndat.tvlauncher.ui.component.card.AppCard
import nl.ndat.tvlauncher.ui.tab.home.AppPopup
import nl.ndat.tvlauncher.ui.tab.home.HomeTabViewModel
import nl.ndat.tvlauncher.util.composition.LocalStartupFocus
import nl.ndat.tvlauncher.util.StartupFocusTarget
import nl.ndat.tvlauncher.util.modifier.ifElse
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppCardRow(
	apps: List<App>,
	modifier: Modifier = Modifier,
	titleRes: Int? = R.string.favorite_apps,
	subtitleRes: Int? = null,
	startupFocusTarget: StartupFocusTarget? = null,
) {
	val viewModel = koinViewModel<HomeTabViewModel>()
	val startupFocus = LocalStartupFocus
	val effectiveTarget = startupFocusTarget ?: StartupFocusTarget.FAVORITE
	val preferredIndex = startupFocus
		?.takeIf { it.target == effectiveTarget && it.itemId != null }
		?.itemId
		?.let { savedId -> apps.indexOfFirst { app -> app.id == savedId }.takeIf { index -> index >= 0 } }
		?: if (effectiveTarget == StartupFocusTarget.FAVORITE) viewModel.preferredFavoriteIndex(apps) else 0

	val cardRowTitle = titleRes?.let { stringResource(it) }
	val cardRowSubtitle = subtitleRes?.let { stringResource(it) }

	CardRow(
		modifier = modifier,
		title = cardRowTitle,
		subtitle = cardRowSubtitle,
		firstItemFocusRequester = startupFocus
			?.takeIf { it.target == effectiveTarget }
			?.requester,
	) { childFocusRequester ->
		itemsIndexed(
			items = apps,
			key = { _, app -> app.id },
		) { index, app ->
			Box(
				modifier = Modifier
					.animateItem()
			) {
				AppCard(
					app = app,
					onFocused = {
						viewModel.rememberFocusedApp(app.id)
						viewModel.recordOpenedApp(app.id)
					},
					modifier = Modifier
						.ifElse(
							condition = index == preferredIndex,
							positiveModifier = Modifier.focusRequester(childFocusRequester)
						),
					popupContent = { firstActionModifier, onAction ->
						AppPopup(
							isFirst = index == 0,
							isLast = index == apps.size - 1,
							isFavorite = app.favoriteOrder != null,
							onToggleFavorite = { favorite ->
								viewModel.favoriteApp(app, favorite)
							},
							onMove = { relativePosition ->
								val destinationApp = apps.getOrNull(index + relativePosition)
								val newIndex = when {
									destinationApp?.favoriteOrder != null -> destinationApp.favoriteOrder
									else -> index + relativePosition.toLong()
								}
								viewModel.setFavoriteOrder(app, newIndex)
							},
							favoriteFocusModifier = firstActionModifier,
							onAction = onAction,
						)
					}
				)
			}
		}
	}

	LaunchedEffect(apps.isNotEmpty(), startupFocus?.pending, startupFocus?.target) {
		if (apps.isNotEmpty() && startupFocus?.pending == true && startupFocus.target == effectiveTarget) {
			startupFocus.requester.requestFocus()
			startupFocus.onFocused()
		}
	}
}
