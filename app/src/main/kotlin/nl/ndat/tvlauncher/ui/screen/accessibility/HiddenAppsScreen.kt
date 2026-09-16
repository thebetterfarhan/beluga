package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HiddenAppsScreen(
	modifier: Modifier = Modifier
) {
	val viewModel = koinViewModel<HiddenAppsViewModel>()
	val hiddenApps by viewModel.hiddenApps.collectAsState()
	val title = stringResource(R.string.hidden_apps)
	val summary = stringResource(R.string.hidden_apps_summary)
	val noApps = stringResource(R.string.no_hidden_apps)
	val unhideLabel = stringResource(R.string.unhide_app)
	val focusRequester = remember { FocusRequester() }
	val initialFocusPending = remember { mutableStateOf(true) }

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 48.dp)
			.focusRestorer(focusRequester)
			.semantics {
				paneTitle = title
			},
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
				text = summary,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(bottom = 8.dp),
			)
		}

		if (hiddenApps.isEmpty()) {
			item {
				Text(
					text = noApps,
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
					modifier = Modifier.padding(vertical = 16.dp),
				)
			}
		}

		items(
			items = hiddenApps,
			key = { app -> app.id },
		) { app ->
			val requester = remember { FocusRequester() }
			Card(
				onClick = { viewModel.unhide(app.id) },
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(requester)
					.onPlaced {
						if (initialFocusPending.value) {
							val accepted = requester.requestFocus()
							debugLauncherLog("hidden-apps entry focus accepted=$accepted")
							if (accepted) initialFocusPending.value = false
						}
					}
					.onFocusChanged {
						debugLauncherLog("hidden-apps: app=${app.displayName} focused=${it.isFocused}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "${app.displayName}. $unhideLabel"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(
						text = app.displayName,
						style = MaterialTheme.typography.titleMedium,
					)
					Text(
						text = unhideLabel,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
	}
}
