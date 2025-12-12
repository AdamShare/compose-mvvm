package com.share.external.lib.navigation.stack

import android.os.Build

/**
 * Removes all entries after the specified [key] from this [LinkedHashMap].
 *
 * This function is used for navigation stack operations like `popTo`, where all entries
 * above a certain point need to be removed.
 *
 * @param key The key to pop to.
 * @param inclusive If `true`, also removes the entry with the specified [key].
 * @return A list of removed values in the order they were removed (most recent first).
 */
fun <K, V> LinkedHashMap<K, V>.removeAllAfter(key: K, inclusive: Boolean = false): List<V> {
    if (!containsKey(key)) {
        return listOf()
    }
    val removed = mutableListOf<V>()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val reversedEntrySet = sequencedEntrySet().reversed().iterator()
        var element = reversedEntrySet.next()
        while (element.key != key) {
            removed.add(element.value)
            reversedEntrySet.remove()
            element = reversedEntrySet.next()
        }
        if (inclusive) {
            removed.add(element.value)
            reversedEntrySet.remove()
        }
        removed
    } else {
        val iterator = entries.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == key) {
                if (inclusive) {
                    removed.add(entry.value)
                    iterator.remove()
                }
                break
            }
        }

        while (iterator.hasNext()) {
            removed.add(iterator.next().value)
            iterator.remove()
        }

        removed.asReversed()
    }
}

/**
 * Removes and returns the last entry from this [LinkedHashMap].
 *
 * Uses the Android 15+ sequenced collections API when available, falling back to
 * a compatible implementation for older API levels.
 *
 * @return The removed value, or `null` if the map was empty.
 */
fun <K, V> LinkedHashMap<K, V>.removeLast(): V? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        if (isEmpty()) null else sequencedValues().removeLast()
    } else {
        keys.lastOrNull()?.let { remove(key = it) }
    }
}
