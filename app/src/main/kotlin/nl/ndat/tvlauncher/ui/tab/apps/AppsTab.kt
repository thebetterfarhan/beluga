package nl.ndat.tvlauncher.ui.tab.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.ui.component.card.AppCard
import nl.ndat.tvlauncher.util.StartupFocusTarget
import nl.ndat.tvlauncher.util.composition.LocalStartupFocus
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppsTab(
	modifier: Modifier = Modifier
) {
	val viewModel = koinViewModel<AppsTabViewModel>()
	val apps by viewModel.apps.collectAsState()
	val startupFocus = LocalStartupFocus
	val firstCardFocusRequester = remember { FocusRequester() }
	val restoredTargetId = startupFocus
		?.takeIf { it.target == StartupFocusTarget.ALL_APPS_TAB }
		?.itemId
	val targetApp = restoredTargetId
		?.let { savedId -> apps.firstOrNull { it.id == savedId } }
		?: apps.firstOrNull()
	val gridState = rememberLazyGridState(viewModel.appsScrollIndex())

	LaunchedEffect(gridState.firstVisibleItemIndex) {
		viewModel.rememberAppsScrollIndex(gridState.firstVisibleItemIndex)
	}

	LazyVerticalGrid(
		state = gridState,
		contentPadding = PaddingValues(
			vertical = 16.dp,
			horizontal = 48.dp,
		),
		verticalArrangement = Arrangement.spacedBy(14.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		columns = GridCells.Adaptive(90.dp * (16f / 9f)),
		modifier = modifier
			.focusRestorer()
			.fillMaxSize()
	) {
		item(
			key = "all-apps-heading",
			span = { GridItemSpan(maxLineSpan) },
		) {
			Text(
				text = stringResource(R.string.tab_apps),
				modifier = Modifier.semantics { heading() },
			)
		}
		items(
			items = apps,
			key = { app -> app.id },
		) { app ->
			Box(
				modifier = Modifier
					.animateItem()
			) {
				AppCard(
					app = app,
					onFocused = { viewModel.rememberFocusedApp(app.id) },
					modifier = if (app == targetApp && startupFocus?.target == StartupFocusTarget.ALL_APPS_TAB) {
						Modifier.focusRequester(firstCardFocusRequester)
					} else Modifier,
					popupContent = { firstActionModifier, onAction ->
						AppPopup(
							isFavorite = app.favoriteOrder != null,
							firstActionModifier = firstActionModifier,
							onToggleFavorite = { favorite ->
								viewModel.favoriteApp(app, favorite)
							},
							onAction = onAction,
						)
					}
				)
			}
		}
	}

	LaunchedEffect(apps.isNotEmpty(), startupFocus?.pending, startupFocus?.target) {
		if (apps.isNotEmpty() && startupFocus?.pending == true && startupFocus.target == StartupFocusTarget.ALL_APPS_TAB) {
			firstCardFocusRequester.requestFocus()
			startupFocus.onFocused()
		}
	}
}
