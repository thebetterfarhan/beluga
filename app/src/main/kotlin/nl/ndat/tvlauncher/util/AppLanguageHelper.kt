package nl.ndat.tvlauncher.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

object AppLanguageHelper {
	private val SUPPORTED_LANGUAGES = listOf(
		LanguageOption("en", "English"),
		LanguageOption("es", "Español"),
		LanguageOption("fr", "Français"),
		LanguageOption("de", "Deutsch"),
		LanguageOption("it", "Italiano"),
		LanguageOption("pt", "Português"),
		LanguageOption("zh", "中文"),
		LanguageOption("ja", "日本語"),
		LanguageOption("ko", "한국어"),
		LanguageOption("ar", "العربية"),
		LanguageOption("hi", "हिन्दी"),
		LanguageOption("ru", "Русский"),
	)

	fun getSupportedLanguages(): List<LanguageOption> = SUPPORTED_LANGUAGES

	fun openSystemLanguageSettings(context: Context): Boolean {
		return try {
			val intent = when {
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
					Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
						data = android.net.Uri.fromParts("package", context.packageName, null)
					}
				}
				else -> {
					Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
						data = android.net.Uri.fromParts("package", context.packageName, null)
					}
				}
			}
			context.startActivity(intent)
			true
		} catch (e: Exception) {
			try {
				context.startActivity(Intent(Settings.ACTION_SETTINGS))
				true
			} catch (_: Exception) {
				false
			}
		}
	}

	data class LanguageOption(
		val code: String,
		val displayName: String,
	)
}
