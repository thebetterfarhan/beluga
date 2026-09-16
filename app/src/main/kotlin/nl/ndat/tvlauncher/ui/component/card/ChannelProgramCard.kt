package nl.ndat.tvlauncher.ui.component.card

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import coil.compose.AsyncImage
import nl.ndat.tvlauncher.data.sqldelight.ChannelProgram
import nl.ndat.tvlauncher.util.debugTrace
import nl.ndat.tvlauncher.util.modifier.debugFocusLog

private fun ChannelProgram.accessibleLabel(): String {
	val name = title?.takeIf { it.isNotBlank() }
		?: episodeTitle?.takeIf { it.isNotBlank() }
		?: "Program"
	val details = buildList {
		episodeTitle?.takeIf { it.isNotBlank() && it != name }?.let { add(it) }
		val episode = listOfNotNull(
			seasonNumber?.takeIf { it.isNotBlank() }?.let { "Season $it" },
			episodeNumber?.takeIf { it.isNotBlank() }?.let { "Episode $it" },
		).joinToString(", ")
		if (episode.isNotBlank()) add(episode)
		val duration = durationMillis
		val playbackPosition = lastPlaybackPositionMillis
		if (duration != null && playbackPosition != null && duration > 0 && playbackPosition > 0) {
			val progress = (playbackPosition * 100 / duration).coerceIn(0, 100)
			add("$progress% watched")
		}
	}
	return listOf(name, details.joinToString(", ")).filter { it.isNotBlank() }.joinToString(". ")
}

@Composable
fun ChannelProgramCard(
	program: ChannelProgram,
	modifier: Modifier = Modifier,
	baseHeight: Dp = 100.dp,
) {
	val context = LocalContext.current
	val programLabel = remember(program.id) {
		debugTrace("compose:program-label-${program.id}") { program.accessibleLabel() }
	}

	Card(
		modifier = modifier
			.height(baseHeight)
			.aspectRatio(program.posterArtAspectRatio?.floatValue ?: 1f)
			.debugFocusLog("program-card:${program.id}")
			// A program name is the actionable item's label. Including the row name
			// here made TalkBack repeat "Watch Next" for every program in the row.
			.semantics { contentDescription = programLabel
				role = Role.Button
			},
		border = CardDefaults.border(
			focusedBorder = Border(
				border = BorderStroke(2.dp, MaterialTheme.colorScheme.border),
			)
		),
		scale = CardDefaults.scale(focusedScale = 1f),
onClick = {
			if (program.intentUri != null) {
				try {
					context.startActivity(Intent.parseUri(program.intentUri, 0))
				} catch (err: Throwable) {
					android.util.Log.w("LauncherFocus", "program-card:${program.id}: launch failed: ${err.message}")
				}
			}
		},
	) {
		AsyncImage(
			modifier = Modifier.fillMaxSize(),
			model = program.posterArtUri,
			contentDescription = null,
			contentScale = ContentScale.Crop,
		)
	}
}
