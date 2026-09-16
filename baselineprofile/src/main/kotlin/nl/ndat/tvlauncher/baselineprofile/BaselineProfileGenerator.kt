package nl.ndat.tvlauncher.baselineprofile

import android.content.Intent
import android.view.KeyEvent
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

/**
 * Generates the launcher's baseline/startup profile.
 *
 * Run on the connected Chromecast with
 * `:app:generateReleaseBaselineProfile --android.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile`
 * (or simply `:app:generateBaselineProfile`).
 *
 * The scenario below covers the launcher cold-start path: process start,
 * the main launcher window, and the initial focus/composition of the Home tab.
 * Keep this scenario representative of what a real cold start does; adding
 * rare code paths here pulls unused classes into the profile.
 */
class BaselineProfileGenerator {
	@get:Rule
	val baselineProfileRule = BaselineProfileRule()

	@Test
	fun generateStartupBaselineProfile() {
		baselineProfileRule.collect(
			packageName = "nl.ndat.tvlauncher",
		) {
			// Start from a warm Home so cold-start work belongs to the target activity.
			pressHome()

			startActivityAndWait(
				Intent().setClassName(
					"nl.ndat.tvlauncher",
					"nl.ndat.tvlauncher.LauncherActivity",
				),
			)

			device.wait(
				Until.hasObject(By.pkg("nl.ndat.tvlauncher").depth(20)),
				60_000,
			)

			// A few D-pad movements exercise the initial card-row focus and
			// composition paths that run during the first seconds on-device.
			repeat(times = 4) {
				device.pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT)
			}
			device.pressKeyCode(KeyEvent.KEYCODE_DPAD_UP)

			// Give recomposition and artwork loading a moment before collection ends.
			device.waitForIdle()
		}
	}
}
