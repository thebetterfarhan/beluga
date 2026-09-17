import java.util.Properties

plugins {
	alias(libs.plugins.android.app)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.sqldelight)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.androidx.baselineprofile)
}

// Release signing secrets live in local.properties (git-ignored).
// Keys: beluga.storeFile, beluga.storePassword, beluga.keyAlias, beluga.keyPassword
val keystoreProperties = Properties().apply {
	val localProps = rootProject.file("local.properties")
	if (localProps.exists()) {
		localProps.inputStream().use { load(it) }
	}
}

kotlin {
	jvmToolchain(libs.versions.java.jdk.get().toInt())
}

android {
	namespace = "nl.ndat.tvlauncher"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	defaultConfig {
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()

		applicationId = "nl.ndat.tvlauncher"
		versionCode = 1_00_00
		versionName = "1.0.0"
	}

	buildFeatures {
		buildConfig = true
		compose = true
	}

	buildTypes {
		// Build type used only by baseline-profile generation: debug-signed but
		// otherwise release-like, so the generator can install and run it.
		create("benchmark") {
			initWith(getByName("debug"))
			matchingFallbacks += listOf("release")
		}
	}

	// Configure release signing from local.properties (git-ignored).
	// Without those properties the release build stays unsigned.
	signingConfigs {
		create("release") {
			storeFile = keystoreProperties.getProperty("beluga.storeFile")?.let(rootProject::file)
			storePassword = keystoreProperties.getProperty("beluga.storePassword")
			keyAlias = keystoreProperties.getProperty("beluga.keyAlias")
			keyPassword = keystoreProperties.getProperty("beluga.keyPassword")
		}
	}
	buildTypes.getByName("release").signingConfig = signingConfigs.getByName("release")
}

baselineProfile {
	// Generated profiles are committed in-tree so release builds pick them up
	// without requiring a device during every build.
	saveInSrc = true
	// Keep a single profile in the main source set; the launcher only ships release.
	mergeIntoMain = true
}

sqldelight {
	databases {
		create("Database") {
			packageName.set("nl.ndat.tvlauncher.data.sqldelight")
			generateAsync.set(true)
		}
	}
}

dependencies {
	// System
	implementation(libs.bundles.androidx.core)
	implementation(libs.bundles.koin)
	implementation(libs.androidx.profileinstaller)
	implementation(libs.androidx.tvprovider)
	implementation(libs.timber)

	// Data
	implementation(libs.bundles.sqldelight)
	implementation(libs.kotlinx.serialization.json)

	// UI
	implementation(libs.bundles.androidx.compose)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.navigation3.ui)
	implementation(libs.androidx.palette)
	implementation(libs.androidx.savedstate)
	implementation(libs.androidx.tv.material)
	implementation(libs.coil)
	implementation(libs.coil.compose)
	debugImplementation(libs.androidx.compose.ui.tooling)
	testImplementation(libs.junit)
	baselineProfile(project(":baselineprofile"))
}
