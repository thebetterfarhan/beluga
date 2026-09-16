package nl.ndat.tvlauncher.ui.tab.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.resolver.ChannelResolver
import nl.ndat.tvlauncher.ui.tab.home.row.AppCardRow
import nl.ndat.tvlauncher.ui.tab.home.row.ChannelProgramCardRow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeTab(
	modifier: Modifier = Modifier
) {
	val viewModel = koinViewModel<HomeTabViewModel>()
	val apps by viewModel.apps.collectAsState()
	val channels by viewModel.channels.collectAsState()
	val watchNextPrograms by viewModel.watchNextPrograms.collectAsState()
	val listState = rememberLazyListState(viewModel.homeScrollIndex())

	LaunchedEffect(listState.firstVisibleItemIndex) {
		viewModel.rememberHomeScrollIndex(listState.firstVisibleItemIndex)
	}

LazyColumn(
		state = listState,
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.focusRestorer()
			.fillMaxSize()
	) {
		// Hide a "Favorites" section when there are no cards in it.
		if (apps.isNotEmpty()) {
			item(key = "apps") {
				AppCardRow(
					apps = apps
				)
			}
		}

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
			val app = remember(channel.packageName, apps) {
				apps.firstOrNull { app -> app.packageName == channel.packageName }
			}
			val programs by viewModel.channelPrograms(channel).collectAsState(initial = emptyList())

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
