package com.share.external.lib.navigation.stack

import co.touchlab.kermit.Logger

/**
 * Logs the current state of a navigation stack for debugging purposes.
 *
 * Outputs a formatted string showing all entries in the stack with their navigation keys
 * and optional metadata (e.g., presentation style).
 *
 * Example output: `Backstack Main[{Home: FullScreen} ⇨ {Settings: Modal}]`
 *
 * @param entries The list of navigation stack entries to log.
 * @param name A human-readable name for the stack (used in log output).
 * @param metadata Optional function to extract additional metadata for each entry.
 */
fun <T : NavigationStackEntryViewProvider> Logger.logEntries(
    entries: List<T>,
    name: String,
    metadata: (T) -> String? = { null }
) = d {
    buildString {
        append("Backstack $name[")
        entries.forEachIndexed { i, provider ->
            append("{${provider.navigationKey.name}")
            metadata(provider)?.let { append(": $it") }
            append("}")
            if (i < entries.size - 1) append(" ⇨ ")
        }
        append("]")
    }
}
