package nl.ndat.tvlauncher.ui.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Toolbar(
	modifier: Modifier = Modifier,
) {
	// No toolbar-level focusRestorer: the tab row owns its own restore scope, and
	// an outer scope used to land Back-from-Apps focus on the Settings icon
	// because that was the most-recent focused descendant overall.
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		ToolbarTabs(modifier = Modifier)
		Spacer(modifier = Modifier.weight(1f))
		ToolbarSettingsButton()
		ToolbarClock()
	}
}
