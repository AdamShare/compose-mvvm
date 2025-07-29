package com.share.external.lib.mvvm.navigation.stack

import android.os.Build


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

fun <K, V> LinkedHashMap<K, V>.removeLast(): V? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        if (isEmpty()) null else sequencedValues().removeLast()
    } else {
        keys.lastOrNull()?.let { remove(key = it) }
    }
}
