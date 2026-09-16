package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nl.ndat.tvlauncher.data.repository.AppRepository
import nl.ndat.tvlauncher.data.repository.ChannelRepository
import nl.ndat.tvlauncher.data.sqldelight.App
import nl.ndat.tvlauncher.data.sqldelight.Channel
import nl.ndat.tvlauncher.util.ChannelPreferences

class ChannelPreferencesViewModel(
	private val channelRepository: ChannelRepository,
	private val appRepository: AppRepository,
	private val channelPreferences: ChannelPreferences,
) : ViewModel() {
	private val storedOrder = channelPreferences.getChannelOrder().toList()

	private val _channels = MutableStateFlow<List<Channel>>(emptyList())
	val channels = _channels.asStateFlow()

	private val _apps = MutableStateFlow<Map<String, App>>(emptyMap())
	val apps = _apps.asStateFlow()

	init {
		loadChannels()
	}

	private fun loadChannels() {
		// Only collect apps for the app-name map; channels are managed via manual mutations
		viewModelScope.launch {
			appRepository.getApps().collect { allApps ->
				_apps.value = allApps.associateBy { it.packageName }
			}
		}
		viewModelScope.launch {
			channelRepository.getFavoriteAppChannels()
				.map { channelList -> orderChannels(channelList) }
				.collect { _channels.value = it }
		}
	}

	private fun orderChannels(channels: List<Channel>): List<Channel> {
		if (storedOrder.isEmpty()) return channels
		val channelMap = channels.associateBy { it.id }
		val ordered = mutableListOf<Channel>()
		val remaining = channelMap.values.toMutableSet()
		for (id in storedOrder) {
			channelMap[id]?.let { ordered.add(it); remaining.remove(it) }
		}
		ordered.addAll(remaining)
		return ordered
	}

	fun moveChannelLeft(channelId: String) {
		val current = _channels.value.toMutableList()
		val index = current.indexOfFirst { it.id == channelId }
		if (index <= 0) return
		val updated = current.toMutableList().apply {
			add(index - 1, removeAt(index))
		}
		_channels.value = updated
		saveOrder(updated)
	}

	fun moveChannelRight(channelId: String) {
		val current = _channels.value.toMutableList()
		val index = current.indexOfFirst { it.id == channelId }
		if (index < 0 || index >= current.size - 1) return
		val updated = current.toMutableList().apply {
			add(index + 1, removeAt(index))
		}
		_channels.value = updated
		saveOrder(updated)
	}

	private fun saveOrder(channels: List<Channel>) {
		channelPreferences.setChannelOrder(channels.map { it.id })
	}
}
