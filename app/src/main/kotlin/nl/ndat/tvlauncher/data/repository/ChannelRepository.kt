package nl.ndat.tvlauncher.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.ndat.tvlauncher.data.DatabaseContainer
import nl.ndat.tvlauncher.data.executeAsListFlow
import nl.ndat.tvlauncher.data.model.ChannelType
import nl.ndat.tvlauncher.data.resolver.ChannelResolver
import nl.ndat.tvlauncher.data.sqldelight.Channel
import nl.ndat.tvlauncher.data.sqldelight.ChannelProgram

class ChannelRepository(
	private val context: Context,
	private val channelResolver: ChannelResolver,
	private val database: DatabaseContainer,
) {
	private suspend fun commitChannels(type: ChannelType, channels: Collection<Channel>) = withContext(Dispatchers.IO) {
		val existingChannels = database.channels.getByType(type).executeAsList()
		if (existingChannels.associateBy { it.id } == channels.associateBy { it.id }) return@withContext

		database.transaction {
			// Remove channels found in database but not in committed list
			database.channels.getByType(type)
				.executeAsList()
				.map { it.id }
				.subtract(channels.map { it.id }.toSet())
				.forEach { id -> database.channels.removeById(id) }

			// Upsert channels
			channels.forEach { channel -> commitChannel(channel) }
		}
	}

	private suspend fun commitChannel(channel: Channel) = withContext(Dispatchers.IO) {
		if (database.channels.getById(channel.id).executeAsOneOrNull() == channel) return@withContext

		database.channels.upsert(
			id = channel.id,
			type = channel.type,
			channelId = channel.channelId,
			displayName = channel.displayName,
			description = channel.description,
			packageName = channel.packageName,
			appLinkIntentUri = channel.appLinkIntentUri,
		).await()
	}

	private suspend fun commitChannelPrograms(
		channelId: String,
		programs: Collection<ChannelProgram>,
	) = withContext(Dispatchers.IO) {
		val existingPrograms = database.channelPrograms.getByChannel(channelId).executeAsList()
		if (existingPrograms.associateBy { it.id } == programs.associateBy { it.id }) return@withContext

		database.transaction {
			// Remove channels found in database but not in committed list
			database.channelPrograms.getByChannel(channelId)
				.executeAsList()
				.map { it.id }
				.subtract(programs.map { it.id }.toSet())
				.forEach { id -> database.channelPrograms.removeById(id) }

			// Upsert channels
			programs.forEach { program -> commitChannelProgram(program) }
		}
	}

	private suspend fun commitChannelPrograms(
		programsByChannel: Collection<Pair<String, Collection<ChannelProgram>>>,
	) = withContext(Dispatchers.IO) {
		val changedProgramsByChannel = programsByChannel.filter { (channelId, programs) ->
			val existingPrograms = database.channelPrograms.getByChannel(channelId).executeAsList()
			existingPrograms.associateBy { it.id } != programs.associateBy { it.id }
		}
		if (changedProgramsByChannel.isEmpty()) return@withContext

		// Commit all program updates in one transaction so the affected queries emit once
		// instead of once per channel, which reduces recomposition and focus churn on refresh.
		database.transaction {
			changedProgramsByChannel.forEach { (channelId, programs) ->
				// Remove programs found in database but not in committed list
				database.channelPrograms.getByChannel(channelId)
					.executeAsList()
					.map { it.id }
					.subtract(programs.map { it.id }.toSet())
					.forEach { id -> database.channelPrograms.removeById(id) }

				// Upsert programs
				programs.forEach { program -> commitChannelProgram(program) }
			}
		}
	}

	private suspend fun commitChannelProgram(program: ChannelProgram) = withContext(Dispatchers.IO) {
		if (database.channelPrograms.getById(program.id).executeAsOneOrNull() == program) return@withContext

		database.channelPrograms.upsert(
			id = program.id,
			channelId = program.channelId,
			packageName = program.packageName,
			weight = program.weight,
			type = program.type,
			posterArtUri = program.posterArtUri,
			posterArtAspectRatio = program.posterArtAspectRatio,
			lastPlaybackPositionMillis = program.lastPlaybackPositionMillis,
			durationMillis = program.durationMillis,
			releaseDate = program.releaseDate,
			itemCount = program.itemCount,
			interactionType = program.interactionType,
			interactionCount = program.interactionCount,
			author = program.author,
			genre = program.genre,
			live = program.live,
			startTimeUtcMillis = program.startTimeUtcMillis,
			endTimeUtcMillis = program.endTimeUtcMillis,
			title = program.title,
			episodeTitle = program.episodeTitle,
			seasonNumber = program.seasonNumber,
			episodeNumber = program.episodeNumber,
			description = program.description,
			intentUri = program.intentUri,
		).await()
	}

	suspend fun refreshAllChannels() {
		refreshWatchNextChannels()
		refreshPreviewChannels()
	}

	suspend fun refreshPreviewChannels() {
		val channels = channelResolver.getPreviewChannels(context)
		commitChannels(ChannelType.PREVIEW, channels)

		// Read all programs up front, then commit them inside a single transaction so each
		// watched channel-program query emits only once for the whole preview refresh.
		val programsByChannel = channels.map { channel ->
			channel.id to channelResolver.getChannelPrograms(context, channel.channelId)
		}
		commitChannelPrograms(programsByChannel)
	}

	suspend fun refreshWatchNextChannels() {
		val channel = Channel(
			id = ChannelResolver.CHANNEL_ID_WATCH_NEXT,
			type = ChannelType.WATCH_NEXT,
			channelId = -1,
			displayName = "",
			description = null,
			packageName = "",
			appLinkIntentUri = null,
		)
		val programs = channelResolver.getWatchNextPrograms(context)

		commitChannel(channel)
		commitChannelPrograms(channel.id, programs)
	}

	suspend fun refreshChannelPrograms(channel: Channel) {
		val programs = channelResolver.getChannelPrograms(context, channel.channelId)
		commitChannelPrograms(channel.id, programs)
	}

	fun getChannels() = database.channels.getAll().executeAsListFlow()

	fun getFavoriteAppChannels() = database.channels.getFavoriteAppChannels(::Channel).executeAsListFlow()

	fun getProgramsByChannel(channel: Channel) = database.channelPrograms.getByChannel(channel.id).executeAsListFlow()

	fun getWatchNextPrograms() =
		database.channelPrograms.getByChannel(ChannelResolver.CHANNEL_ID_WATCH_NEXT).executeAsListFlow()
}
