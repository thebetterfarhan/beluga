package nl.ndat.tvlauncher.ui.screen.accessibility

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R
import nl.ndat.tvlauncher.data.Destinations
import nl.ndat.tvlauncher.util.DefaultLauncherHelper
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
	val orientationHelp = stringResource(R.string.orientation_help)
	val orientationHelpSummary = stringResource(R.string.orientation_help_summary)
	val googleTvLauncher = stringResource(R.string.google_tv_launcher)
	val googleTvLauncherSummary = stringResource(R.string.google_tv_launcher_summary)
	val hiddenApps = stringResource(R.string.hidden_apps)
	val hiddenAppsSummary = stringResource(R.string.hidden_apps_summary)
	val homeLayout = stringResource(R.string.home_layout)
	val homeLayoutSummary = stringResource(R.string.home_layout_summary)
	val appLanguage = stringResource(R.string.app_language)
	val appLanguageSummary = stringResource(R.string.app_language_summary)
	val context = LocalContext.current

	// Stock launcher state changes only on the App-Info page; refresh on RESUMED.
	val googleTvHelper = remember(context) { GoogleTvLauncherHelper(context) }
	val googleTvDisabled = remember { mutableStateOf(googleTvHelper.isDisabledByUser()) }
	val googleTvOwnsHome = remember { mutableStateOf(googleTvHelper.resolvesAsHome()) }
	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(lifecycleOwner, googleTvHelper) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) {
				googleTvDisabled.value = googleTvHelper.isDisabledByUser()
				googleTvOwnsHome.value = googleTvHelper.resolvesAsHome()
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}

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
		if (googleTvHelper.isInstalled) item {
			val googleTvState = when {
				googleTvDisabled.value -> stringResource(R.string.google_tv_launcher_state_disabled)
				googleTvOwnsHome.value -> stringResource(R.string.google_tv_launcher_state_owns_home)
				else -> stringResource(R.string.google_tv_launcher_state_enabled)
			}
			val googleTvSummary = when {
				googleTvDisabled.value -> stringResource(R.string.google_tv_launcher_summary)
				googleTvOwnsHome.value -> stringResource(R.string.google_tv_launcher_owns_home_summary)
				else -> stringResource(R.string.google_tv_launcher_summary)
			}
			Card(
				onClick = {
					debugLauncherLog("google-tv-launcher: state=${if (googleTvDisabled.value) "disabled" else "enabled"} ownsHome=${googleTvOwnsHome.value}")
					if (googleTvDisabled.value) {
						context.startActivity(googleTvHelper.appDetailsIntent())
					} else if (googleTvOwnsHome.value) {
						context.startActivity(Intent(android.provider.Settings.ACTION_HOME_SETTINGS))
					} else {
						context.startActivity(googleTvHelper.appDetailsIntent())
					}
				},
				modifier = Modifier
					.fillMaxWidth()
					.onFocusChanged {
						debugLauncherLog("google-tv-launcher: focused=${it.isFocused} hasFocus=${it.hasFocus}")
					}
					.semantics(mergeDescendants = true) {
						contentDescription = "$googleTvLauncher. $googleTvState. $googleTvSummary"
						role = Role.Button
					},
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(text = googleTvLauncher, style = MaterialTheme.typography.titleMedium)
					Text(
						text = stringResource(R.string.focus_restore_current, googleTvState),
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
					Text(
						text = googleTvSummary,
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.padding(top = 4.dp),
					)
				}
			}
		}
	}
}
