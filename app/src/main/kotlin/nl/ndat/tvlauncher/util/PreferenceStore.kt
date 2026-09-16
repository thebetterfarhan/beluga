package nl.ndat.tvlauncher.util

import android.content.Context

interface PreferenceStore {
	fun getString(key: String, defaultValue: String? = null): String?
	fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
	fun setString(key: String, value: String?)
	fun setBoolean(key: String, value: Boolean)
	fun remove(key: String)
}

class SharedPreferenceStore(context: Context, name: String) : PreferenceStore {
	private val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

	override fun getString(key: String, defaultValue: String?): String? =
		preferences.getString(key, defaultValue)

	override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
		preferences.getBoolean(key, defaultValue)

	override fun setString(key: String, value: String?) {
		preferences.edit().putString(key, value).apply()
	}

	override fun setBoolean(key: String, value: Boolean) {
		preferences.edit().putBoolean(key, value).apply()
	}

	override fun remove(key: String) {
		preferences.edit().remove(key).apply()
	}
}
