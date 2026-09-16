package nl.ndat.tvlauncher.ui.screen.accessibility

import android.provider.Settings
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
import androidx.compose.ui.semantics.clearAndSetSemantics
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
import nl.ndat.tvlauncher.data.Destinations
import nl.ndat.tvlauncher.util.SystemAccessibilityHelper
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
	val focusRestoreCurrentValue = stringResource(R.string.focus_restore_current, currentValue)
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
		val systemA11y = stringResource(R.string.system_accessibility_settings)
		val systemA11ySummary = stringResource(R.string.system_accessibility_settings_summary)
		val audioDesc = stringResource(R.string.audio_description)
		val audioDescOn = stringResource(R.string.audio_description_on)
		val audioDescOff = stringResource(R.string.audio_description_off)
		val audioDescSummary = stringResource(R.string.audio_description_summary)
		val highContrast = stringResource(R.string.high_contrast_text)
		val highContrastOn = stringResource(R.string.high_contrast_text_on)
		val highContrastOff = stringResource(R.string.high_contrast_text_off)
		val highContrastSummary = stringResource(R.string.high_contrast_text_summary)
		val context = LocalContext.current
		val sysA11yHelper = remember { SystemAccessibilityHelper(context) }
		val audioDescState = if (sysA11yHelper.isAudioDescriptionRequested) audioDescOn else audioDescOff
		val highContrastState = if (sysA11yHelper.isHighContrastTextEnabled) highContrastOn else highContrastOff

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
			val cardDesc = "$settingName. $focusRestoreCurrentValue. $summary"
			Card(
				onClick = { backStack.add(Destinations.FocusRestoration) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("settings-overview: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
					Text(text = settingName, style = MaterialTheme.typography.titleMedium)
					Text(
						text = focusRestoreCurrentValue,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			val cardDesc = "$orientationHelp. $orientationHelpSummary"
			Card(
				onClick = { backStack.add(Destinations.OrientationHelp) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("orientation-help: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
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
			val cardDesc = "$hiddenApps. $hiddenAppsSummary"
			Card(
				onClick = { backStack.add(Destinations.HiddenApps) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("hidden-apps: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
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
			val cardDesc = "$homeLayout. $homeLayoutSummary"
			Card(
				onClick = { backStack.add(Destinations.HomePreferences) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("home-layout: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
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
			val cardDesc = "$appLanguage. $appLanguageSummary"
			Card(
				onClick = { backStack.add(Destinations.AppLanguage) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("app-language: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
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
			val cardDesc = "$channelPrefs. $channelPrefsSummary"
			Card(
				onClick = { backStack.add(Destinations.ChannelPreferences) },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("channel-prefs: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
					Text(text = channelPrefs, style = MaterialTheme.typography.titleMedium)
					Text(
						text = channelPrefsSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			val cardDesc = "$systemA11y. $systemA11ySummary"
			Card(
				onClick = {
					val intent = android.content.Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
					context.startActivity(intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
				},
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("system-a11y: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
					Text(text = systemA11y, style = MaterialTheme.typography.titleMedium)
					Text(
						text = systemA11ySummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			val cardDesc = "$audioDesc. $audioDescState. $audioDescSummary"
			Card(
				onClick = { },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("audio-desc: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
					Text(text = audioDesc, style = MaterialTheme.typography.titleMedium)
					Text(
						text = audioDescState,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
					Text(
						text = audioDescSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
		item {
			val cardDesc = "$highContrast. $highContrastState. $highContrastSummary"
			Card(
				onClick = { },
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("high-contrast: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
					Text(text = highContrast, style = MaterialTheme.typography.titleMedium)
					Text(
						text = highContrastState,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
					Text(
						text = highContrastSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
	}
}
