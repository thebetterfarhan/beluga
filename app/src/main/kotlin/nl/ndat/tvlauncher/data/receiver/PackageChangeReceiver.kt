package nl.ndat.tvlauncher.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import nl.ndat.tvlauncher.data.repository.AppRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PackageChangeReceiver : BroadcastReceiver(), KoinComponent {
	companion object {
		private val packageActions = arrayOf(
			Intent.ACTION_PACKAGE_ADDED,
			Intent.ACTION_PACKAGE_CHANGED,
			Intent.ACTION_PACKAGE_REPLACED,
			Intent.ACTION_PACKAGE_REMOVED,
		)
	}

	private val appRepository: AppRepository by inject()

	override fun onReceive(context: Context, intent: Intent) {
		val pendingIntent = goAsync()

		// Per-receive scope: cancel and discard once the work completes.
		val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
		scope.launch {
			try {
				val packageName = when {
					intent.action in packageActions && intent.data?.scheme == "package" -> intent.data?.schemeSpecificPart
					else -> null
				}

				if (packageName != null) appRepository.refreshApplication(packageName)
				else appRepository.refreshAllApplications()
			} catch (err: Throwable) {
				// Silently swallow receiver errors; the next resume or broadcast will retry.
			} finally {
				pendingIntent.finish()
				scope.cancel()
			}
		}
	}
}
