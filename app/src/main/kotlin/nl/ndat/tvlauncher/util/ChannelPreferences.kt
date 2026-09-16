package nl.ndat.tvlauncher.util

import android.content.Context

class ChannelPreferences(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "channel_preferences"))

	@Volatile
	private var cachedOrder: Set<String>? = null

	fun getChannelOrder(): Set<String> {
		cachedOrder?.let { return it }
		val result = store.getStringSet(KEY_CHANNEL_ORDER)
		cachedOrder = result
		return result
	}

	fun setChannelOrder(channelIds: List<String>) {
		store.setStringSet(KEY_CHANNEL_ORDER, channelIds.toSet())
		cachedOrder = channelIds.toSet()
	}

	companion object {
		const val KEY_CHANNEL_ORDER = "channel_order"
	}
}
