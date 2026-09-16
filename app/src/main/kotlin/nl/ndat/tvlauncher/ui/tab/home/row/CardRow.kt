package nl.ndat.tvlauncher.ui.tab.home.row

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardRow(
	modifier: Modifier = Modifier,
	title: String? = null,
	subtitle: String? = null,
	firstItemFocusRequester: FocusRequester? = null,
	content: LazyListScope.(childFocusRequester: FocusRequester) -> Unit,
) = Column(
	modifier = modifier
) {
	if (title != null || subtitle != null) {
		if (title != null) {
			Text(
				text = title,
				fontSize = 18.sp,
				// This remains available as a heading to screen-reader users without
				// becoming a D-pad focus target or being repeated in every card label.
				modifier = Modifier
					.semantics { heading() }
					.padding(
						vertical = 4.dp,
						horizontal = 48.dp,
					)
			)
		}

		if (subtitle != null) {
			Text(
				text = subtitle,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier
					.padding(
						start = 48.dp,
						end = 48.dp,
						top = 2.dp,
						bottom = 6.dp,
					)
			)
		}

		Spacer(modifier = Modifier.height(4.dp))
	}

	val childFocusRequester = firstItemFocusRequester ?: remember { FocusRequester() }

	LazyRow(
		contentPadding = PaddingValues(
			vertical = 16.dp,
			horizontal = 48.dp,
		),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		modifier = Modifier
			.fillMaxWidth()
			.focusRestorer(childFocusRequester),
	) {
		content(childFocusRequester)
	}
}
