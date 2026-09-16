package nl.ndat.tvlauncher.ui.tab.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import nl.ndat.tvlauncher.R

@Composable
fun AppPopup(
	isFirst: Boolean,
	isLast: Boolean,
	isFavorite: Boolean,
	onToggleFavorite: (favorite: Boolean) -> Unit,
	onMove: (relativePosition: Int) -> Unit,
	favoriteFocusModifier: Modifier = Modifier,
	onAction: () -> Unit,
) {
	val focusRequester = remember { FocusRequester() }
	Row(
		horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
		modifier = Modifier.focusRestorer(focusRequester),
	) {
		IconButton(
			enabled = !isFirst,
			modifier = Modifier.size(IconButtonDefaults.SmallButtonSize),
			onClick = { onAction(); onMove(-1) },
		) {
			Icon(
				imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
				contentDescription = stringResource(R.string.move_app_left),
				modifier = Modifier.size(IconButtonDefaults.SmallIconSize)
			)
		}

		IconButton(
			modifier = favoriteFocusModifier
				.size(IconButtonDefaults.SmallButtonSize),
			onClick = { onAction(); onToggleFavorite(!isFavorite) },
		) {
			Icon(
				imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
				contentDescription = stringResource(
					if (isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites
				),
				modifier = Modifier.size(IconButtonDefaults.SmallIconSize)
			)
		}

		IconButton(
			enabled = !isLast,
			modifier = Modifier.size(IconButtonDefaults.SmallButtonSize),
			onClick = { onAction(); onMove(+1) },
		) {
			Icon(
				imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
				contentDescription = stringResource(R.string.move_app_right),
				modifier = Modifier.size(IconButtonDefaults.SmallIconSize)
			)
		}
	}
}
