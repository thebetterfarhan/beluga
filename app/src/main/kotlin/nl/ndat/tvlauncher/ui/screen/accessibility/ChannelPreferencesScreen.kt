package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChannelPreferencesScreen(
	modifier: Modifier = Modifier
) {
	val viewModel = koinViewModel<ChannelPreferencesViewModel>()
	val channels by viewModel.channels.collectAsState()
	val apps by viewModel.apps.collectAsState()
	val title = stringResource(R.string.channel_preferences)
	val summary = stringResource(R.string.channel_preferences_summary)
	val focusRequester = remember { FocusRequester() }

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 48.dp)
			.focusRestorer(focusRequester)
			.semantics { paneTitle = title },
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		item {
			Text(
				text = title,
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(top = 12.dp).semantics { heading() },
			)
		}
		item {
			Text(
				text = summary,
				style = MaterialTheme.typography.bodyMedium,
			)
		}

		if (channels.isEmpty()) {
			item {
				Text(
					text = stringResource(R.string.no_channels),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
					modifier = Modifier.padding(vertical = 16.dp),
				)
			}
		}

		itemsIndexed(
			items = channels,
			key = { _, channel -> channel.id }
		) { index, channel ->
			val app = apps[channel.packageName]
			val channelName = app?.displayName ?: channel.displayName
			ChannelOrderCard(
				channelName = channelName,
				channelDescription = channel.description ?: "",
				canMoveLeft = index > 0,
				canMoveRight = index < channels.size - 1,
				onMoveLeft = { viewModel.moveChannelLeft(channel.id) },
				onMoveRight = { viewModel.moveChannelRight(channel.id) },
			)
		}
	}
}

@Composable
private fun ChannelOrderCard(
	channelName: String,
	channelDescription: String,
	canMoveLeft: Boolean,
	canMoveRight: Boolean,
	onMoveLeft: () -> Unit,
	onMoveRight: () -> Unit,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		onClick = { },
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = channelName,
					style = MaterialTheme.typography.titleMedium,
				)
				if (channelDescription.isNotBlank()) {
					Text(
						text = channelDescription,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = if (canMoveLeft) "◀" else " ",
					fontSize = 18.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier
						.width(48.dp)
						.semantics {
							contentDescription = if (canMoveLeft) "Move $channelName left" else ""
							role = Role.Button
						}
						.clickable(enabled = canMoveLeft, onClick = onMoveLeft)
						.padding(8.dp),
				)
				Text(
					text = if (canMoveRight) "▶" else " ",
					fontSize = 18.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier
						.width(48.dp)
						.semantics {
							contentDescription = if (canMoveRight) "Move $channelName right" else ""
							role = Role.Button
						}
						.clickable(enabled = canMoveRight, onClick = onMoveRight)
						.padding(8.dp),
				)
			}
		}
	}
}
