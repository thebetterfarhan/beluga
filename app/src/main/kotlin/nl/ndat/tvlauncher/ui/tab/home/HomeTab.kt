package nl.ndat.tvlauncher.ui.tab.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.resolver.ChannelResolver
import nl.ndat.tvlauncher.ui.tab.home.row.AppCardRow
import nl.ndat.tvlauncher.ui.tab.home.row.ChannelProgramCardRow
import org.koin.androidx.compose.koinViewModel

private const val RecentRowCap = 4

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeTab(
	modifier: Modifier = Modifier
) {
	val viewModel = koinViewModel<HomeTabViewModel>()
	val favorites by viewModel.apps.collectAsState()
	val allApps by viewModel.allApps.collectAsState()
	val channels by viewModel.channels.collectAsState()
	val watchNextPrograms by viewModel.watchNextPrograms.collectAsState()
	val recentAppIds by viewModel.recentAppIds.collectAsState()
	val lastFocusedAppId by viewModel.lastFocusedAppId.collectAsState()
	val showContinue by viewModel.showContinue.collectAsState()
	val showRecent by viewModel.showRecent.collectAsState()
	val showWatchNext by viewModel.showWatchNext.collectAsState()
	val channelProgramsMap by viewModel.channelProgramsMap.collectAsState()
	val listState = rememberLazyListState(viewModel.homeScrollIndex())

	LaunchedEffect(listState.firstVisibleItemIndex) {
		viewModel.rememberHomeScrollIndex(listState.firstVisibleItemIndex)
	}

	val continueApp = remember(allApps, recentAppIds, lastFocusedAppId) {
		val focusedId = lastFocusedAppId ?: return@remember null
		allApps.firstOrNull { it.id == focusedId }
			?: recentAppIds.firstNotNullOfOrNull { id -> allApps.firstOrNull { it.id == id } }
	}
	val recentApps = remember(recentAppIds, allApps) {
		recentAppIds
			.mapNotNull { id -> allApps.firstOrNull { it.id == id } }
			.distinctBy { it.id }
			.take(RecentRowCap)
	}
	val anyChannels = channels.isNotEmpty() || watchNextPrograms.isNotEmpty()

	// Announce tab structure to TalkBack on every launcher resume.
	val lifecycleOwner = LocalLifecycleOwner.current
	var resumeCount by remember { mutableStateOf(0L) }
	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) resumeCount++
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}
	var homeAnnouncement by remember { mutableStateOf<String?>(null) }
	val announceHomeDefault = stringResource(R.string.announce_home_tab)
	LaunchedEffect(resumeCount, continueApp, recentApps, favorites, anyChannels, showContinue, showRecent, showWatchNext, announceHomeDefault) {
		val sections = buildList {
			if (continueApp != null && showContinue) add("Continue")
			if (recentApps.isNotEmpty() && showRecent) add("Recent")
			if (favorites.isNotEmpty()) add("Favorites")
			if (anyChannels && showWatchNext) add("Watch Next")
		}
		homeAnnouncement = if (sections.isEmpty()) {
			announceHomeDefault
		} else {
			"Home tab. ${sections.joinToString(", ")}."
		}
	}

	LazyColumn(
		state = listState,
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.focusRestorer()
			.fillMaxSize()
	) {
		if (continueApp != null && showContinue) {
			item(key = "continue-${continueApp.id}") {
				HeadingText(R.string.home_continue)
				AppCardRow(
					apps = listOf(continueApp),
					titleRes = null,
					subtitleRes = R.string.home_continue_subtitle,
				)
			}
		}

		if (recentApps.isNotEmpty() && showRecent) {
			item(key = "recent") {
				HeadingText(R.string.home_recent)
				AppCardRow(
					apps = recentApps,
					titleRes = null,
					subtitleRes = R.string.home_recent_subtitle,
				)
			}
		}

		if (favorites.isNotEmpty()) {
			item(key = "favorites") {
				HeadingText(R.string.favorite_apps)
				AppCardRow(
					apps = favorites,
					titleRes = null,
				)
			}
		}

		if (anyChannels && showWatchNext) {
			if (watchNextPrograms.isNotEmpty()) {
				item(
					key = ChannelResolver.CHANNEL_ID_WATCH_NEXT
				) {
					ChannelProgramCardRow(
						title = stringResource(R.string.channel_watch_next),
						programs = watchNextPrograms,
					)
				}
			}

			items(
				items = channels,
				key = { channel -> channel.id }
			) { channel ->
				val app = remember(channel.packageName, allApps) {
					allApps.firstOrNull { app -> app.packageName == channel.packageName }
				}
				val programs = channelProgramsMap[channel.id] ?: emptyList()

				if (app != null) {
					val title = stringResource(R.string.channel_preview, app.displayName, channel.displayName)

					ChannelProgramCardRow(
						title = title,
						programs = programs,
					)
				}
			}
		}
	}

	homeAnnouncement?.let { announcement ->
		Box(
			modifier = Modifier.clearAndSetSemantics { contentDescription = announcement }
		) {
			Text(text = "", style = MaterialTheme.typography.bodyMedium)
		}
	}
}

@Composable
private fun HeadingText(textRes: Int) {
	Text(
		text = stringResource(textRes),
		style = MaterialTheme.typography.titleMedium,
		modifier = Modifier
			.padding(
				horizontal = 48.dp,
				vertical = 4.dp,
			)
			.semantics { heading() },
	)
}
