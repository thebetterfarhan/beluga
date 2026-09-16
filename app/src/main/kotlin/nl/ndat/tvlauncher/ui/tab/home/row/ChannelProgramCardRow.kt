package nl.ndat.tvlauncher.ui.tab.home.row

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import nl.ndat.tvlauncher.data.sqldelight.ChannelProgram
import nl.ndat.tvlauncher.ui.component.card.ChannelProgramCard
import nl.ndat.tvlauncher.util.debugTrace
import nl.ndat.tvlauncher.util.modifier.ifElse

@Composable
fun ChannelProgramCardRow(
	modifier: Modifier = Modifier,
	title: String,
	programs: List<ChannelProgram>,
) {
	// A card without an intent looks selectable but cannot perform an action. Do
	// not expose its row (or its heading) when it contains only those entries.
	val actionablePrograms = programs.filter { !it.intentUri.isNullOrBlank() }
	val focusedProgramState = remember { mutableStateOf<ChannelProgram?>(null) }

	if (actionablePrograms.isNotEmpty()) {
		CardRow(
			title = title,
			modifier = modifier,
		) { childFocusRequester ->
			itemsIndexed(
				items = actionablePrograms,
				key = { _, program -> program.id },
			) { index, program ->
		val onProgramFocusChanged: (FocusState) -> Unit = remember(program, focusedProgramState) {
			{ state ->
				debugTrace("focus-write:program-${program.id}") {
					val focusedProgram = focusedProgramState.value
					when {
						state.hasFocus && focusedProgram != program -> focusedProgramState.value = program
						!state.hasFocus && focusedProgram == program -> focusedProgramState.value = null
					}
				}
			}
		}
				Box {
					ChannelProgramCard(
						program = program,
						modifier = Modifier
							.ifElse(
								condition = index == 0,
								positiveModifier = Modifier.focusRequester(childFocusRequester)
							)
							.onFocusChanged(onProgramFocusChanged),
					)
				}
			}
		}

		// This updates for every D-pad move. Rendering it directly avoids starting a
		// transition and measuring both the old and new detail panels on each move.
		// Keep the state read in a child restart scope so detail changes do not
		// invalidate the lazy row and its visible cards.
		FocusedChannelProgramCardDetails(focusedProgramState)
	}
}
