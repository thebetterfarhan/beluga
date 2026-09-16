package nl.ndat.tvlauncher.util

import android.content.Context

class ChannelPreferences(private val store: PreferenceStore) {
	constructor(context: Context) : this(SharedPreferenceStore(context, "channel_preferences"))

	fun getChannelOrder(): Set<String> = store.getStringSet(KEY_CHANNEL_ORDER)

	fun setChannelOrder(channelIds: List<String>) {
		store.setStringSet(KEY_CHANNEL_ORDER, channelIds.toSet())
	}

	companion object {
		const val KEY_CHANNEL_ORDER = "channel_order"
	}
}
