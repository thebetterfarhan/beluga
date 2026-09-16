package nl.ndat.tvlauncher.util.modifier

import android.util.Log
import android.os.SystemClock
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import nl.ndat.tvlauncher.BuildConfig

private const val FocusLogTag = "LauncherFocus"
private const val DpadToFocusWindowMs = 500L
private var lastDpadNanos = 0L

fun debugDpadLog(keyCode: Int) {
	if (BuildConfig.DEBUG) {
		lastDpadNanos = SystemClock.elapsedRealtimeNanos()
		Log.d(FocusLogTag, "dpad: key=$keyCode")
	}
}

fun debugFocusRequestLog(target: String) {
	if (BuildConfig.DEBUG) debugLauncherLog("focus-request: $target")
}

fun debugLauncherLog(event: String) {
	if (BuildConfig.DEBUG) Log.d(FocusLogTag, event)
}

/** Emits focused/unfocused transitions for device validation without release logging. */
fun Modifier.debugFocusLog(target: String): Modifier = onFocusChanged { state ->
	if (BuildConfig.DEBUG && (state.isFocused || state.hasFocus)) {
		val elapsedMs = if (lastDpadNanos == 0L) null else
			((SystemClock.elapsedRealtimeNanos() - lastDpadNanos) / 1_000_000)
				.takeIf { it <= DpadToFocusWindowMs }
		debugLauncherLog("focus: $target${elapsedMs?.let { ", dpad-to-focus=${it}ms" } ?: ""}")
	}
}
