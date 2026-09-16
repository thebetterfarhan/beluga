package nl.ndat.tvlauncher.util.modifier

import androidx.compose.ui.Modifier

fun Modifier.ifElse(
	condition: () -> Boolean,
	positiveModifier: Modifier,
	negativeModifier: Modifier = Modifier,
): Modifier = then(if (condition()) positiveModifier else negativeModifier)

fun Modifier.ifElse(
	condition: Boolean,
	positiveModifier: Modifier,
	negativeModifier: Modifier = Modifier,
): Modifier = then(if (condition) positiveModifier else negativeModifier)

