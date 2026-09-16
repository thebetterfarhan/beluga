package nl.ndat.tvlauncher.ui.screen.accessibility

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.Destinations
import nl.ndat.tvlauncher.util.AccessibilityServicesHelper
import nl.ndat.tvlauncher.util.DefaultLauncherHelper
import nl.ndat.tvlauncher.util.composition.LocalBackStack
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccessibilityOverviewScreen(modifier: Modifier = Modifier) {
	val viewModel = koinViewModel<AccessibilitySettingsViewModel>()
	val backStack = LocalBackStack.current
	val focusRequester = remember { FocusRequester() }
	val title = stringResource(R.string.accessibility)
	val settingName = stringResource(R.string.focus_restoration)
	val currentValue = stringResource(viewModel.focusRestoreMode.nameRes())
	val summary = stringResource(R.string.focus_restore_summary)
	val orientationHelp = stringResource(R.string.orientation_help)
	val orientationHelpSummary = stringResource(R.string.orientation_help_summary)
	val hiddenApps = stringResource(R.string.hidden_apps)
	val hiddenAppsSummary = stringResource(R.string.hidden_apps_summary)
	val homeLayout = stringResource(R.string.home_layout)
	val homeLayoutSummary = stringResource(R.string.home_layout_summary)
	val appLanguage = stringResource(R.string.app_language)
	val appLanguageSummary = stringResource(R.string.app_language_summary)
	val channelPrefs = stringResource(R.string.channel_preferences)
	val channelPrefsSummary = stringResource(R.string.channel_preferences_summary)
	val context = LocalContext.current

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
				modifier = Modifier.padding(top = 12.dp).semantics { heading() },
			)
		}
		item {
			Card(
				onClick = { backStack.add(Destinations.FocusRestoration) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("settings-overview: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "$settingName. $currentValue. $summary"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = settingName, style = MaterialTheme.typography.titleMedium)
					Text(
						text = stringResource(R.string.focus_restore_current, currentValue),
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			Card(
				onClick = { backStack.add(Destinations.OrientationHelp) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("orientation-help: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "$orientationHelp. $orientationHelpSummary"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = orientationHelp, style = MaterialTheme.typography.titleMedium)
					Text(
						text = orientationHelpSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			Card(
				onClick = { backStack.add(Destinations.HiddenApps) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("hidden-apps: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "$hiddenApps. $hiddenAppsSummary"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = hiddenApps, style = MaterialTheme.typography.titleMedium)
					Text(
						text = hiddenAppsSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			Card(
				onClick = { backStack.add(Destinations.HomePreferences) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("home-layout: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "$homeLayout. $homeLayoutSummary"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = homeLayout, style = MaterialTheme.typography.titleMedium)
					Text(
						text = homeLayoutSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			Card(
				onClick = { backStack.add(Destinations.AppLanguage) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("app-language: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "$appLanguage. $appLanguageSummary"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = appLanguage, style = MaterialTheme.typography.titleMedium)
					Text(
						text = appLanguageSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			val defaultLauncherHelper = remember(context) { DefaultLauncherHelper(context) }
			val isDefaultLauncher = remember { defaultLauncherHelper.isDefaultLauncher() }
			val talkBackEnabled = remember { AccessibilityServicesHelper.isTalkBackEnabled(context) }
			val setAsDefaultLauncher = stringResource(R.string.set_as_default_launcher)
			val setAsDefaultLauncherSummary = stringResource(R.string.set_as_default_launcher_summary)
			val defaultLauncherSummary = stringResource(R.string.default_launcher_active_summary)
			val defaultLauncherState = if (isDefaultLauncher) {
				stringResource(R.string.default_launcher_active)
			} else {
				stringResource(R.string.default_launcher_not_active)
			}
			val cardSummary = when {
				isDefaultLauncher -> defaultLauncherSummary
				talkBackEnabled -> stringResource(R.string.set_as_default_launcher_summary_talkback_on)
				else -> setAsDefaultLauncherSummary
			}
			val cardContentDesc = "$setAsDefaultLauncher. $cardSummary"
			Card(
				onClick = {
					debugLauncherLog("default-launcher: isDefault=$isDefaultLauncher talkBack=$talkBackEnabled")
					val intent = defaultLauncherHelper.requestDefaultLauncherIntent()
					if (intent != null) {
						context.startActivity(intent)
					}
				},
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("default-launcher: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardContentDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = setAsDefaultLauncher, style = MaterialTheme.typography.titleMedium)
					Text(
						text = defaultLauncherState,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
					Text(
						text = cardSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			Card(
				onClick = { backStack.add(Destinations.ChannelPreferences) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("channel-prefs: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "$channelPrefs. $channelPrefsSummary"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = channelPrefs, style = MaterialTheme.typography.titleMedium)
					Text(
						text = channelPrefsSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
	}
}
