package nl.ndat.tvlauncher

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import nl.ndat.tvlauncher.data.DatabaseContainer
import nl.ndat.tvlauncher.data.repository.AppRepository
import nl.ndat.tvlauncher.data.repository.ChannelRepository
import nl.ndat.tvlauncher.data.resolver.AppResolver
import nl.ndat.tvlauncher.data.resolver.ChannelResolver
import nl.ndat.tvlauncher.ui.tab.apps.AppsTabViewModel
import nl.ndat.tvlauncher.ui.tab.home.HomeTabViewModel
import nl.ndat.tvlauncher.ui.screen.accessibility.AccessibilitySettingsViewModel
import nl.ndat.tvlauncher.ui.screen.accessibility.ChannelPreferencesViewModel
import nl.ndat.tvlauncher.ui.screen.accessibility.HiddenAppsViewModel
import nl.ndat.tvlauncher.ui.screen.accessibility.HomePreferencesViewModel
import nl.ndat.tvlauncher.ui.screen.launcher.LauncherScreenViewModel
import nl.ndat.tvlauncher.util.DefaultLauncherHelper
import nl.ndat.tvlauncher.util.AccessibilityPreferences
import nl.ndat.tvlauncher.util.HiddenAppsStore
import nl.ndat.tvlauncher.util.HomePreferences
import nl.ndat.tvlauncher.util.ChannelPreferences
import nl.ndat.tvlauncher.util.LastFocusedAppStore
import nl.ndat.tvlauncher.util.RecentAppsStore
import nl.ndat.tvlauncher.util.FocusRestorationManager
import nl.ndat.tvlauncher.util.LauncherStateStore
import nl.ndat.tvlauncher.util.LauncherStateRecorder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

private val launcherModule = module {
	single { DefaultLauncherHelper(get()) }
	single { AccessibilityPreferences(context = get()) }
	single { FocusRestorationManager(preferences = get()) }
	single { LastFocusedAppStore(context = get()) }
	single { RecentAppsStore(context = get()) }
	single { HiddenAppsStore(context = get()) }
	single { HomePreferences(context = get()) }
	single { ChannelPreferences(context = get()) }
	single { LauncherStateStore(context = get()) }
	single { LauncherStateRecorder(store = get()) }

	single { AppRepository(get(), get(), get()) }
	single { AppResolver() }

	single { ChannelRepository(get(), get(), get()) }
	single { ChannelResolver() }

	viewModel { HomeTabViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
	viewModel { AppsTabViewModel(get(), get(), get()) }
	viewModel { AccessibilitySettingsViewModel(get()) }
	viewModel { HiddenAppsViewModel(get(), get()) }
	viewModel { HomePreferencesViewModel(get()) }
	viewModel { ChannelPreferencesViewModel(get(), get(), get()) }
	viewModel { LauncherScreenViewModel(get(), get()) }
}

private val databaseModule = module {
	// Create database(s)
	single { DatabaseContainer(get()) }
}

class LauncherApplication : Application(), ImageLoaderFactory {
	override fun onCreate() {
		super.onCreate()

		Timber.plant(Timber.DebugTree())

		startKoin {
			androidLogger(level = if (BuildConfig.DEBUG) Level.DEBUG else Level.INFO)
			androidContext(this@LauncherApplication)

			modules(launcherModule, databaseModule)
		}
	}

	override fun newImageLoader() = ImageLoader.Builder(this)
		.logger(DebugLogger())
		.build()
}
