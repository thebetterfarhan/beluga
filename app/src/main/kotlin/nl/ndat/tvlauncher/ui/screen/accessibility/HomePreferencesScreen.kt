package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomePreferencesScreen(
	modifier: Modifier = Modifier
) {
	val viewModel = koinViewModel<HomePreferencesViewModel>()
	val showContinue by viewModel.showContinue.collectAsState()
	val showRecent by viewModel.showRecent.collectAsState()
	val showWatchNext by viewModel.showWatchNext.collectAsState()

	val title = stringResource(R.string.home_preferences)
	val continueLabel = stringResource(R.string.home_preferences_continue)
	val continueDesc = stringResource(R.string.home_preferences_continue_desc)
	val recentLabel = stringResource(R.string.home_preferences_recent)
	val recentDesc = stringResource(R.string.home_preferences_recent_desc)
	val watchNextLabel = stringResource(R.string.home_preferences_watch_next)
	val watchNextDesc = stringResource(R.string.home_preferences_watch_next_desc)

	val focusRequester = remember { FocusRequester() }
	val initialFocusPending = remember { mutableStateOf(true) }

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
				modifier = Modifier
					.padding(top = 12.dp)
					.semantics { heading() },
			)
		}
		item {
			Text(
				text = stringResource(R.string.home_preferences_summary),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(bottom = 8.dp),
			)
		}

		item {
			RowToggle(
				label = continueLabel,
				description = continueDesc,
				checked = showContinue,
				onCheckedChange = { viewModel.setShowContinue(it) },
				initialFocusPending = initialFocusPending.value,
				focusRequester = focusRequester,
			)
		}
		item {
			RowToggle(
				label = recentLabel,
				description = recentDesc,
				checked = showRecent,
				onCheckedChange = { viewModel.setShowRecent(it) },
				initialFocusPending = initialFocusPending.value,
				focusRequester = focusRequester,
			)
		}
		item {
			RowToggle(
				label = watchNextLabel,
				description = watchNextDesc,
				checked = showWatchNext,
				onCheckedChange = { viewModel.setShowWatchNext(it) },
				initialFocusPending = initialFocusPending.value,
				focusRequester = focusRequester,
			)
		}
	}
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RowToggle(
	label: String,
	description: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	initialFocusPending: Boolean,
	focusRequester: FocusRequester,
) {
	val requester = remember { FocusRequester() }
	val onLabel = stringResource(R.string.enabled)
	val offLabel = stringResource(R.string.disabled)
	val state = if (checked) onLabel else offLabel

	// Exactly-once focus request on first composition.
	LaunchedEffect(initialFocusPending) {
		if (initialFocusPending) {
			requester.requestFocus()
		}
	}

	Card(
		onClick = { onCheckedChange(!checked) },
		modifier = Modifier
			.fillMaxWidth()
			.focusRequester(requester)
			.onFocusChanged {
				debugLauncherLog("home-prefs: focused=${it.isFocused}")
			}
			.semantics(mergeDescendants = true) {
				contentDescription = "$label. $description. $state"
				role = Role.Switch
				stateDescription = state
				selected = checked
			},
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(20.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = label,
					style = MaterialTheme.typography.titleMedium,
				)
				Text(
					text = description,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
					modifier = Modifier.padding(top = 4.dp),
				)
			}
			Switch(
				checked = checked,
				onCheckedChange = null,
				modifier = Modifier.padding(start = 16.dp),
			)
		}
	}
}
