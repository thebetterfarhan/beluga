package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.util.FocusRestoreMode
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FocusRestorationScreen(modifier: Modifier = Modifier) {
	val viewModel = koinViewModel<AccessibilitySettingsViewModel>()
	val selectedMode = viewModel.focusRestoreMode
	val entrySelectedMode = remember { selectedMode }
	val title = stringResource(R.string.focus_restoration)
	val optionFocusRequesters = remember {
		FocusRestoreMode.entries.associateWith { FocusRequester() }
	}
	val initialFocusPending = remember { mutableStateOf(true) }

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 48.dp)
			.focusRestorer()
			.semantics {
				paneTitle = title
			},
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
				text = stringResource(R.string.focus_restore_summary),
				style = MaterialTheme.typography.bodyMedium,
			)
		}
		items(FocusRestoreMode.entries, key = { it.name }) { option ->
			val requester = optionFocusRequesters.getValue(option)
			FocusRestoreOption(
				mode = option,
				selected = selectedMode == option,
				modifier = Modifier.focusRequester(requester).onPlaced {
					if (entrySelectedMode == option && initialFocusPending.value) {
						val accepted = requester.requestFocus()
						debugLauncherLog("settings-entry: $option request accepted=$accepted")
						if (accepted) initialFocusPending.value = false
					}
				}.onFocusChanged {
					if (it.isFocused) {
						debugLauncherLog("settings-option: focus=$option")
					}
				},
				onClick = { viewModel.selectFocusRestoreMode(option) },
			)
		}
	}
}

@Composable
private fun FocusRestoreOption(
	mode: FocusRestoreMode,
	selected: Boolean,
	modifier: Modifier,
	onClick: () -> Unit,
) {
	val name = stringResource(mode.nameRes())
	val description = stringResource(mode.descriptionRes())
	// Inner Text stays visible but silent for TalkBack; the merged
	// contentDescription carries the single announcement per option.
	Card(
		onClick = onClick,
		modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {
			contentDescription = name
			stateDescription = description
			this.selected = selected
			role = Role.RadioButton
		},
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Text(
				text = name,
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.clearAndSetSemantics { },
			)
			Text(
				text = description,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(top = 4.dp).clearAndSetSemantics { },
			)
		}
	}
}

fun FocusRestoreMode.nameRes() = when (this) {
	FocusRestoreMode.LAST_FOCUSED_APP -> R.string.focus_restore_last_focused_app
	FocusRestoreMode.FIRST_FAVORITE -> R.string.focus_restore_first_favorite
	FocusRestoreMode.HOME_TAB -> R.string.focus_restore_home_tab
	FocusRestoreMode.ALL_APPS_TAB -> R.string.focus_restore_all_apps_tab
	FocusRestoreMode.COMPLETE_LAUNCHER_STATE -> R.string.focus_restore_complete_launcher_state
}

fun FocusRestoreMode.descriptionRes() = when (this) {
	FocusRestoreMode.LAST_FOCUSED_APP -> R.string.focus_restore_last_focused_app_description
	FocusRestoreMode.FIRST_FAVORITE -> R.string.focus_restore_first_favorite_description
	FocusRestoreMode.HOME_TAB -> R.string.focus_restore_home_tab_description
	FocusRestoreMode.ALL_APPS_TAB -> R.string.focus_restore_all_apps_tab_description
	FocusRestoreMode.COMPLETE_LAUNCHER_STATE -> R.string.focus_restore_complete_launcher_state_description
}
