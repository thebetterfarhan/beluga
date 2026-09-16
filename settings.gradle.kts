enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "tv-launcher"

include(":app")
include(":baselineprofile")

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

	repositories {
		mavenCentral()
		google()
	}
}
