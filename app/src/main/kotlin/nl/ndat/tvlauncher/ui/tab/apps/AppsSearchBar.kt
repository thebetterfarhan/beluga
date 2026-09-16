package nl.ndat.tvlauncher.ui.tab.apps

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R

/**
 * Accessible search bar for the All Apps tab.
 *
 * - `contentDescription` names the field so TalkBack announces "Search apps" on focus.
 * - Clears immediately on clear-button activation; focus returns to the field.
 * - Does not auto-submit or navigate on Enter — D-pad DOWN exits the field into the grid.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppsSearchBar(
	query: String,
	onQueryChange: (String) -> Unit,
	onClear: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val searchLabel = stringResource(R.string.search_apps)
	val clearLabel = stringResource(R.string.search_clear)

		val focusRequester = remember { FocusRequester() }
	var isFocused by remember { mutableStateOf(false) }

	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 48.dp, vertical = 8.dp),
	) {
		// Search icon — decorative, silent for TalkBack
		Icon(
			imageVector = Icons.Filled.Search,
			contentDescription = null,
			modifier = Modifier
				.padding(top = 14.dp, end = 12.dp)
				.size(20.dp),
			tint = if (isFocused) {
				MaterialTheme.colorScheme.onSurfaceVariant
			} else {
				MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
			},
		)

		BasicTextField(
			value = query,
			onValueChange = onQueryChange,
			textStyle = TextStyle(
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 16.sp,
			),
			decorationBox = { innerTextField ->
				Text(
					text = if (query.isEmpty()) stringResource(R.string.search_apps_placeholder) else "",
					style = TextStyle(
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
						fontSize = 16.sp,
					),
				)
				innerTextField()
			},
			modifier = Modifier
				.weight(1f)
				.focusRequester(focusRequester)
				.onFocusChanged { isFocused = it.isFocused }
				.semantics {
					contentDescription = searchLabel
				},
			singleLine = true,
		)

		// Clear button — visible only when query has text
		if (query.isNotEmpty()) {
			IconButton(
				onClick = {
					onClear()
					focusRequester.requestFocus()
				},
				modifier = Modifier
					.size(IconButtonDefaults.SmallButtonSize)
					.semantics { contentDescription = clearLabel },
			) {
				Icon(
					imageVector = Icons.Filled.Clear,
					contentDescription = clearLabel,
					modifier = Modifier.size(IconButtonDefaults.SmallIconSize),
				)
			}
		}
	}
}
