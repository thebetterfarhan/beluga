package nl.ndat.tvlauncher.util

import android.os.Trace
import nl.ndat.tvlauncher.BuildConfig

@PublishedApi
internal const val MaxSectionNameLength = 127

/**
 * Runs [block] inside a synchronous atrace section so device traces can attribute
 * main-thread cost to a named launcher path. Release builds skip instrumentation
 * entirely; the section always ends, including when [block] throws.
 */
inline fun <T> debugTrace(section: String, block: () -> T): T {
	if (!BuildConfig.DEBUG) {
		return block()
	}
	Trace.beginSection(section.take(MaxSectionNameLength))
	try {
		return block()
	} finally {
		Trace.endSection()
	}
}
