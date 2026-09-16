plugins {
	id("com.android.test") version libs.versions.android.plugin.get()
	alias(libs.plugins.androidx.baselineprofile)
}

kotlin {
	jvmToolchain(libs.versions.java.jdk.get().toInt())
}

android {
	namespace = "nl.ndat.tvlauncher.baselineprofile"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	defaultConfig {
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()

		targetProjectPath = ":app"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}

baselineProfile {
	// Profile generation runs against the connected Chromecast (wireless ADB).
	useConnectedDevices = true
}

dependencies {
	implementation(libs.androidx.benchmark.macro.junit4)
	implementation(libs.androidx.test.ext.junit)
	implementation(libs.androidx.test.runner)
	implementation(libs.androidx.uiautomator)
}
