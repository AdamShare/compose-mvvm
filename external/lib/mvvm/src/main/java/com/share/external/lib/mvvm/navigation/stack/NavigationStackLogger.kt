package com.share.external.lib.mvvm.navigation.stack

import co.touchlab.kermit.Logger

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