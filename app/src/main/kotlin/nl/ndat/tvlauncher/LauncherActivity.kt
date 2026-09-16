package nl.ndat.tvlauncher

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.tvprovider.media.tv.TvContractCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import nl.ndat.tvlauncher.data.repository.AppRepository
import nl.ndat.tvlauncher.data.repository.ChannelRepository
import nl.ndat.tvlauncher.ui.AppBase
import nl.ndat.tvlauncher.util.DefaultLauncherHelper
import nl.ndat.tvlauncher.util.LauncherStateRecorder
import nl.ndat.tvlauncher.util.RefreshStalenessTracker
import nl.ndat.tvlauncher.util.modifier.debugLauncherLog
import org.koin.android.ext.android.inject

@SuppressLint("RestrictedApi")
val PERMISSION_READ_CHANNELS = TvContractCompat.PERMISSION_READ_TV_LISTINGS
val PERMISSIONS = listOf(PERMISSION_READ_CHANNELS)

class LauncherActivity : ComponentActivity() {
	private val defaultLauncherHelper: DefaultLauncherHelper by inject()
	private val appRepository: AppRepository by inject()
	private val channelRepository: ChannelRepository by inject()
	private val launcherStateRecorder: LauncherStateRecorder by inject()

	private val appsRefreshStaleness = RefreshStalenessTracker(REFRESH_STALE_MS)
	private val channelsRefreshStaleness = RefreshStalenessTracker(REFRESH_STALE_MS)

	companion object {
		private const val REFRESH_STALE_MS = 60_000L
	}

	private val permissionsLauncher =
		registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
			// Refresh channels when permission is granted
			if (permissions[PERMISSION_READ_CHANNELS] == true) lifecycleScope.launch { channelRepository.refreshAllChannels() }
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			AppBase()
		}

		validateDefaultLauncher()

		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.RESUMED) {
				// Live app changes are already handled by PackageChangeReceiver. Only re-query
				// when the cached data is stale so a quick return to the launcher does not trigger
				// a full apps/channels refresh and the resulting recomposition and focus churn.
				debugLauncherLog("resumed: refresh staleness check")
				try {
					val now = SystemClock.elapsedRealtime()
					val appsStale = appsRefreshStaleness.isStale(now)
					if (appsStale) {
						val start = SystemClock.elapsedRealtime()
						appRepository.refreshAllApplications()
						appsRefreshStaleness.markSuccessfulRefresh(SystemClock.elapsedRealtime())
						debugLauncherLog("resumed: apps refreshed in ${SystemClock.elapsedRealtime() - start}ms")
					}

					val nowChannels = SystemClock.elapsedRealtime()
					val channelsStale = channelsRefreshStaleness.isStale(nowChannels)
					if (channelsStale) {
						val start = SystemClock.elapsedRealtime()
						channelRepository.refreshAllChannels()
						channelsRefreshStaleness.markSuccessfulRefresh(SystemClock.elapsedRealtime())
						debugLauncherLog("resumed: channels refreshed in ${SystemClock.elapsedRealtime() - start}ms")
					}
					debugLauncherLog("resumed: refresh appsStale=$appsStale channelsStale=$channelsStale")
				} catch (err: CancellationException) {
					throw err
				} catch (err: Throwable) {
					debugLauncherLog("resumed: refresh failed: ${err::class.simpleName}: ${err.message ?: "(no message)"}")
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()

		// Request missing permissions
		val missingPermissions = PERMISSIONS
			.filter { permission -> checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED }
			.toTypedArray()
		if (missingPermissions.isNotEmpty()) permissionsLauncher.launch(missingPermissions)
	}

	override fun onStop() {
		super.onStop()

		// Flush the recorded destination and focused item once per session, not on every focus change.
		launcherStateRecorder.flush()
	}

	private fun validateDefaultLauncher() {
		if (!defaultLauncherHelper.isDefaultLauncher() && defaultLauncherHelper.canRequestDefaultLauncher()) {
			val intent = defaultLauncherHelper.requestDefaultLauncherIntent()
			@Suppress("DEPRECATION")
			if (intent != null) startActivityForResult(intent, 0)
		}
	}

	@SuppressLint("RestrictedApi")
	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
		// Map "menu" key to a long press on the dpad because the compose TV library doesn't do that yet
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			event.startTracking()
			val longPressEvent = KeyEvent(
				SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(),
				KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_DPAD_CENTER,
				1
			)
			return dispatchKeyEvent(longPressEvent)
		}

		return super.onKeyDown(keyCode, event)
	}
}
