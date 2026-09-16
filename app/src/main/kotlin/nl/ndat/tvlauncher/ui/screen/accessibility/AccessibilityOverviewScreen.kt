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
import nl.ndat.tvlauncher.util.AccessibilityServicesHelper
import nl.ndat.tvlauncher.util.GoogleTvLauncherHelper
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
			val homeRemapEnabled = remember { AccessibilityServicesHelper.isHomeRemapEnabled(context) }
			val homeRemap = stringResource(R.string.home_remap)
			val homeRemapState = if (homeRemapEnabled) stringResource(R.string.home_remap_enabled) else stringResource(R.string.home_remap_disabled)
			val homeRemapSummary = if (homeRemapEnabled) stringResource(R.string.home_remap_summary_enabled) else stringResource(R.string.home_remap_summary_disabled)
			val cardDesc = "$homeRemap. $homeRemapState. $homeRemapSummary"
			Card(
				onClick = {
					debugLauncherLog("home-remap: enabled=$homeRemapEnabled")
					val intent = android.content.Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
					context.startActivity(intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
				},
				modifier = Modifier
					.fillMaxWidth()
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
					Text(text = homeRemap, style = MaterialTheme.typography.titleMedium)
					Text(
						text = homeRemapState,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
					Text(
						text = homeRemapSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
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
			val googleTvHelper = remember(context) { GoogleTvLauncherHelper(context) }
			val isInstalled = remember { googleTvHelper.isInstalled }
			val isDisabled = remember { googleTvHelper.isDisabledByUser() }
			val resolvesAsHome = remember { googleTvHelper.resolvesAsHome() }
			val googleTvLauncher = stringResource(R.string.google_tv_launcher)
			val googleTvState = when {
				!isInstalled -> return@item
				isDisabled -> stringResource(R.string.google_tv_launcher_state_disabled)
				resolvesAsHome -> stringResource(R.string.google_tv_launcher_state_owns_home)
				else -> stringResource(R.string.google_tv_launcher_state_enabled)
			}
			val googleTvSummary = when {
				!isInstalled -> return@item
				isDisabled -> ""
			 resolvesAsHome -> stringResource(R.string.google_tv_launcher_owns_home_summary)
				else -> stringResource(R.string.google_tv_launcher_summary)
			}
			val cardDesc = if (googleTvSummary.isNotEmpty()) "$googleTvLauncher. $googleTvState. $googleTvSummary" else "$googleTvLauncher. $googleTvState"
			Card(
				onClick = {
					debugLauncherLog("google-tv-launcher: disabled=$isDisabled resolvesHome=$resolvesAsHome")
					context.startActivity(googleTvHelper.appDetailsIntent())
				},
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("google-tv-launcher: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = cardDesc
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp).clearAndSetSemantics { }) {
					Text(text = googleTvLauncher, style = MaterialTheme.typography.titleMedium)
					Text(
						text = googleTvState,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
					if (googleTvSummary.isNotEmpty()) {
						Text(
							text = googleTvSummary,
							style = MaterialTheme.typography.bodyMedium,
							modifier = Modifier.padding(top = 4.dp),
						)
					}
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
	}
}
