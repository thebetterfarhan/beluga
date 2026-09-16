package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.platform.LocalContext
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
import nl.ndat.tvlauncher.util.AppLanguageHelper
import nl.ndat.tvlauncher.util.AppLanguageHelper.LanguageOption
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog

@Composable
fun AppLanguageScreen(
	modifier: Modifier = Modifier
) {
	val title = stringResource(R.string.app_language)
	val summary = stringResource(R.string.app_language_summary)
	val systemSettingsLabel = stringResource(R.string.app_language_system_settings)
	val systemSettingsDesc = stringResource(R.string.app_language_system_settings_desc)
	val context = LocalContext.current
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
		items(AppLanguageHelper.getSupportedLanguages(), key = { it.code }) { language ->
			LanguageOptionCard(language = language)
		}
		item {
			Card(
				onClick = {
					val opened = AppLanguageHelper.openSystemLanguageSettings(context)
					debugLauncherLog("app-language: system settings opened=$opened")
				},
				modifier = Modifier
					.fillMaxWidth()
					.semantics(mergeDescendants = true) {
						contentDescription = "$systemSettingsLabel. $systemSettingsDesc"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(
						text = systemSettingsLabel,
						style = MaterialTheme.typography.titleMedium,
					)
					Text(
						text = systemSettingsDesc,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
	}
}

@Composable
private fun LanguageOptionCard(language: LanguageOption) {
	Card(
		onClick = { },
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Text(
				text = language.displayName,
				style = MaterialTheme.typography.titleMedium,
			)
		}
	}
}
