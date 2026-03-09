package com.getbackcompose.navigation.stack

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

    return removed.asReversed()
}

/**
 * Removes and returns the last entry from this [LinkedHashMap].
 *
 * @return The removed value, or `null` if the map was empty.
 */
fun <K, V> LinkedHashMap<K, V>.removeLast(): V? {
    return keys.lastOrNull()?.let { remove(key = it) }
}
