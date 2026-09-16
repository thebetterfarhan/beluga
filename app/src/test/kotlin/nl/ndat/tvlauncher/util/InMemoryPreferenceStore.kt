package nl.ndat.tvlauncher.util

class InMemoryPreferenceStore : PreferenceStore {
	private val data = mutableMapOf<String, Any?>()

	override fun getString(key: String, defaultValue: String?): String? =
		data[key] as? String ?: defaultValue

	override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
		data[key] as? Boolean ?: defaultValue

	override fun setString(key: String, value: String?) {
		data[key] = value
	}

	override fun setBoolean(key: String, value: Boolean) {
		data[key] = value
	}

	override fun getStringSet(key: String, defaultValue: Set<String>): Set<String> =
		@Suppress("UNCHECKED_CAST") (data[key] as? Set<String>) ?: defaultValue

	override fun setStringSet(key: String, value: Set<String>) {
		data[key] = value
	}

	override fun remove(key: String) {
		data.remove(key)
	}

	fun contains(key: String): Boolean = data.containsKey(key)
}
