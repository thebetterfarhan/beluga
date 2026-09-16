package nl.ndat.tvlauncher.ui.screen.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import nl.ndat.tvlauncher.R

@Composable
fun OrientationHelpScreen(modifier: Modifier = Modifier) {
	val title = stringResource(R.string.orientation_help)
	val focusRequester = remember { FocusRequester() }

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 48.dp)
			.focusRestorer(focusRequester)
			.semantics { paneTitle = title },
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		item {
			Text(
				text = title,
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(top = 12.dp).semantics { heading() },
			)
		}
		item {
			Text(
				text = stringResource(R.string.orientation_help_summary),
				style = MaterialTheme.typography.bodyMedium,
			)
		}
		item {
			OrientationHelpSection(
				headingRes = R.string.orientation_help_remote_heading,
				bodyRes = R.string.orientation_help_remote_body,
			)
		}
		item {
			OrientationHelpSection(
				headingRes = R.string.orientation_help_menus_heading,
				bodyRes = R.string.orientation_help_menus_body,
			)
		}
		item {
			OrientationHelpSection(
				headingRes = R.string.orientation_help_back_heading,
				bodyRes = R.string.orientation_help_back_body,
			)
		}
	}
}

@Composable
private fun OrientationHelpSection(headingRes: Int, bodyRes: Int) {
	Column {
		Text(
			text = stringResource(headingRes),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.semantics { heading() },
		)
		Text(
			text = stringResource(bodyRes),
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier.padding(top = 4.dp),
		)
	}
}
