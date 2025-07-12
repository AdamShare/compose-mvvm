package com.share.external.foundation.collections

fun <K, V> doublyLinkedMapOf() = DoublyLinkedHashMap<K, V>()

fun <K, V> doublyLinkedMapOf(vararg pairs: Pair<K, V>): DoublyLinkedHashMap<K, V> {
    val map = DoublyLinkedHashMap<K, V>()
    pairs.forEach { map[it.first] = it.second }
    return map
}

interface DoublyLinkedMap<K, out V> : Map<K, V> {
    override val keys: KeySet<K>
    override val values: Values<V>

    interface Values<out V> : Collection<V> {
        fun lastOrNull(): V?

        fun asReversed(): Iterator<V>
    }

    interface KeySet<K> : Set<K> {
        fun lastOrNull(): K?

        fun asReversed(): Iterator<K>
    }
}

/**
 * While [LinkedHashMap] is doubly linked, it doesn't store a reference to the last entry so there are no APIs available
 * for reverse order mutation operations, which is the most frequent operation on the nav stack.
 *
 * A linked map allows us to make arbitrary removals and reordering simpler than a pure stack/queue implementation and
 * more efficient and easier to manage than an array list based on indices.
 */
class DoublyLinkedHashMap<K, V> : MutableMap<K, V>, DoublyLinkedMap<K, V> {
    private val hashMap = HashMap<K, Entry>()
    private var first: Entry? = null
    private var last: Entry? = null

    override val entries: EntrySet by lazy { EntrySet() }
    override val keys: KeySet by lazy { KeySet() }
    override val size: Int
        get() = hashMap.size

    override val values: Values by lazy { Values() }

    override fun containsValue(value: V): Boolean = first?.firstOrNull { it.value == value } != null

    override fun containsKey(key: K): Boolean = hashMap.containsKey(key)

    override fun get(key: K): V? = hashMap[key]?.value

    override fun isEmpty(): Boolean = first == null

    override fun equals(other: Any?): Boolean {
        if (other === this) return true

        if (other !is Map<*, *>) return false
        if (other.size != size) return false

        for ((key, value) in entries) {
            if (value == null) {
                if (!(other[key] == null && other.containsKey(key))) return false
            } else {
                if (value != other[key]) return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var h = 0
        for (entry in entries) h += entry.hashCode()
        return h
    }

    override fun clear() {
        val toRemove = first
        first = null
        last = null
        toRemove?.forEach { it.remove() }
    }

    override fun remove(key: K): V? {
        val entry = hashMap[key]
        entry?.remove()
        return entry?.value
    }

    fun removeAllAfter(key: K, inclusive: Boolean = false): List<V> {
        if (!containsKey(key)) {
            return listOf()
        }

        val removed = mutableListOf<V>()
        val reversedEntrySet = entries.asReversed()
        var next = reversedEntrySet.next()
        while (next.key != key) {
            removed.add(next.value)
            reversedEntrySet.remove()
            next = reversedEntrySet.next()
        }
        if (inclusive) {
            removed.add(next.value)
            reversedEntrySet.remove()
        }
        return removed.asReversed()
    }

    override fun putAll(from: Map<out K, V>) = from.forEach { put(it.key, it.value) }

    override fun put(key: K, value: V): V? {
        val entry = hashMap[key]
        return if (entry != null) {
            val previousValue = entry.value
            entry.value = value
            entry.moveToLast()
            previousValue
        } else {
            hashMap[key] = Entry(key, value).apply { append() }
            null
        }
    }

    private inner class Entry(override val key: K, override var value: V) :
        MutableMap.MutableEntry<K, V>, Iterable<Entry> {
        var previous: Entry? = null
            private set

        var next: Entry? = null
            private set

        fun moveToLast() {
            if (last == this) {
                return
            }

            removeLink()
            append()
        }

        fun append() {
            next = null

            previous = last
            previous?.next = this

            last = this
            if (previous == null) {
                first = this
            }
        }

        fun remove() {
            removeLink()
            hashMap.remove(key)
        }

        private fun removeLink() {
            previous?.next = next
            next?.previous = previous

            if (this == first) {
                first = next
            }
            if (this == last) {
                last = previous
            }

            next = null
            previous = null
        }

        override fun setValue(newValue: V): V {
            val previous = value
            value = newValue
            return previous
        }

        override fun iterator(): Iterator<Entry> = EntryIterator(this) { it }

        override fun equals(other: Any?): Boolean {
            return (other as? DoublyLinkedHashMap<*, *>.Entry)?.let { entry ->
                key == entry.key && value == entry.value
            } ?: false
        }

        override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)

        override fun toString(): String = "$key=$value"
    }

    private inner class EntryIterator<T>(var next: Entry?, val reversed: Boolean = false, val transform: (Entry) -> T) :
        MutableIterator<T> {
        private var current: Entry? = null

        override fun hasNext(): Boolean = next != null

        override fun next(): T {
            current = next
            next = if (reversed) next?.previous else next?.next
            return current?.run(transform)
                ?: throw NoSuchElementException("DoublyLinkedHashMap.EntryIterator next is null")
        }

        override fun remove() {
            val toRemove = current
            if (toRemove == null) {
                throw NoSuchElementException("DoublyLinkedHashMap.EntryIterator current entry is null")
            } else {
                toRemove.remove()
            }
        }
    }

    inner class Values : AbstractMutableCollection<V>(), DoublyLinkedMap.Values<V> {
        override fun lastOrNull(): V? = last?.value

        override fun iterator(): MutableIterator<V> = EntryIterator(first) { it.value }

        override val size: Int
            get() = hashMap.size

        override fun asReversed(): MutableIterator<V> = EntryIterator(last, reversed = true) { it.value }

        override fun add(element: V): Boolean {
            // Behavior of existing maps is to throw as well.
            throw UnsupportedOperationException("Map must have an associated key, use MutableMap methods")
        }
    }

    inner class KeySet : AbstractMutableSet<K>(), DoublyLinkedMap.KeySet<K> {
        override fun lastOrNull(): K? = last?.key

        override fun iterator(): MutableIterator<K> = EntryIterator(first) { it.key }

        override val size: Int
            get() = hashMap.size

        override fun contains(element: K): Boolean = containsKey(element)

        override fun asReversed(): MutableIterator<K> = EntryIterator(last, reversed = true) { it.key }

        override fun add(element: K): Boolean {
            // Behavior of existing maps is to throw as well.
            throw UnsupportedOperationException("Map must have an associated value, use MutableMap methods")
        }
    }

    inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
        fun lastOrNull(): MutableMap.MutableEntry<K, V>? = last

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntryIterator(first) { it }

        override val size: Int
            get() = hashMap.size

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean = hashMap[element.key] == element

        fun asReversed(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntryIterator(last, reversed = true) { it }

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
            // Behavior of existing maps is to throw as well.
            throw UnsupportedOperationException("Entry cannot be added directly, use MutableMap methods")
        }
    }
}

fun <K, V> DoublyLinkedHashMap<K, V>.removeLast() = keys.lastOrNull()?.let { remove(it) }
