# Project facts

- Purpose: Android TV launcher with remote and TalkBack navigation.
- Source: app/src; Android package nl.ndat.tvlauncher; Kotlin/Compose.
- Configuration: gradle/libs.versions.toml specifies Java 21, minimum SDK 23, compile/target SDK 37.
- Checks: see docs/accessibility-testing.md for the established build, lint, unit-test command and device checklist.
- Debug build: .\gradlew.bat :app:assembleDebug
- Expected APK directory: app/build/outputs/apk/debug (confirm after building).
- Device focus and spoken output require manual verification; no device tests run during configuration installation.
- Next step: choose a specific accessibility issue and use the existing regression checklist.
