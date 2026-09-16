package nl.ndat.tvlauncher.ui.tab.apps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.tv.material3.MaterialTheme
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
	val query by viewModel.searchQuery.collectAsState()
	val recentlyUpdatedCount = viewModel.recentlyUpdatedCount
	val startupFocus = LocalStartupFocus

	val firstCardFocusRequester = remember { FocusRequester() }
	val restoredTargetId = startupFocus
		?.takeIf { it.target == StartupFocusTarget.ALL_APPS_TAB }
		?.itemId
	val targetApp = restoredTargetId
		?.let { savedId -> apps.firstOrNull { app -> app.id == savedId } }
		?: apps.firstOrNull()
	val gridState = rememberLazyGridState(viewModel.appsScrollIndex())
	val gridFocusRequester = remember { FocusRequester() }

	val lifecycleOwner = LocalLifecycleOwner.current
	var resumeCount by remember { mutableStateOf(0L) }
	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) resumeCount++
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}
	val announceAppsTab = stringResource(R.string.announce_apps_tab)
	val announceSingular = stringResource(R.string.search_result_count_singular)
	val announceOneUpdated = stringResource(R.string.announce_one_app_updated)
	val announceAppsUpdated = stringResource(R.string.announce_apps_updated)
	var appsAnnouncement by remember { mutableStateOf<String?>(null) }
	LaunchedEffect(resumeCount, apps, announceAppsTab, recentlyUpdatedCount) {
		appsAnnouncement = buildString {
			append(announceAppsTab)
			if (apps.isNotEmpty()) {
				val countLabel = if (apps.size == 1) announceSingular else "$apps.size apps"
				append(" $countLabel.")
			}
			if (recentlyUpdatedCount > 0) {
				val updatedLabel = if (recentlyUpdatedCount == 1) announceOneUpdated else "$recentlyUpdatedCount $announceAppsUpdated"
				append(" $updatedLabel")
			}
		}
	}

	// Restore focus to the grid on startup when ALL_APPS_TAB is the saved
	// destination, so the user lands in the app grid rather than the search field.
	LaunchedEffect(apps.isNotEmpty(), startupFocus?.pending, startupFocus?.target) {
		if (apps.isNotEmpty() && startupFocus?.pending == true && startupFocus.target == StartupFocusTarget.ALL_APPS_TAB) {
			firstCardFocusRequester.requestFocus()
			startupFocus.onFocused()
		}
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			// paneTitle announces "All apps" when TalkBack enters this pane.
			.semantics { paneTitle = "All apps" },
	) {
		// Search bar — always focusable; DOWN from here goes to the grid.
		AppsSearchBar(
			query = query,
			onQueryChange = viewModel::onSearchQueryChange,
			onClear = viewModel::clearSearch,
			modifier = Modifier.focusRestorer(firstCardFocusRequester),
		)

		// Heading — outside the grid so it does not participate in grid focus
		// order. heading() enables navigation-by-heading; paneTitle is on the
		// outer Column so it announces on pane entry without duplication.
		Text(
			text = stringResource(R.string.tab_apps),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier
				.padding(horizontal = 48.dp, vertical = 4.dp)
				.semantics { heading() },
		)

		if (apps.isEmpty() && query.isNotEmpty()) {
			// Empty search state — announce to TalkBack; invisible to sighted users.
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(48.dp),
				contentAlignment = Alignment.Center,
			) {
				Text(
					text = stringResource(R.string.search_no_results),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
					modifier = Modifier.semantics { },
				)
			}
		}

		LazyVerticalGrid(
			state = gridState,
			contentPadding = PaddingValues(
				start = 48.dp,
				top = 8.dp,
				end = 48.dp,
				bottom = 16.dp,
			),
			verticalArrangement = Arrangement.spacedBy(14.dp),
			horizontalArrangement = Arrangement.spacedBy(14.dp),
			columns = GridCells.Adaptive(90.dp * (16f / 9f)),
			modifier = Modifier.fillMaxSize().focusRestorer(gridFocusRequester),
		) {
			items(
				items = apps,
				key = { app -> app.id },
			) { app ->
				Box(
					modifier = Modifier.animateItem(),
				) {
					AppCard(
						app = app,
						onFocused = {
							viewModel.rememberFocusedApp(app.id)
						},
						modifier = if (app == targetApp && startupFocus?.target == StartupFocusTarget.ALL_APPS_TAB) {
							Modifier.focusRequester(firstCardFocusRequester)
						} else Modifier,
						popupContent = { firstActionModifier, onAction ->
							AppPopup(
								isFavorite = app.favoriteOrder != null,
								isHidden = viewModel.isHidden(app.id),
								firstActionModifier = firstActionModifier,
								onToggleFavorite = { favorite ->
									viewModel.favoriteApp(app, favorite)
								},
								onHide = { viewModel.hideApp(app.id) },
								onAction = onAction,
							)
						},
					)
				}
			}
		}
	}

	appsAnnouncement?.let { announcement ->
		Box(
			modifier = Modifier.clearAndSetSemantics { contentDescription = announcement }
		) {
			Text(text = "", style = MaterialTheme.typography.bodyMedium)
		}
	}
}
