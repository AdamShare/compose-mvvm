package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Stable

@Stable
interface NavigationBackStack<K> {
    val size: Int
    fun pop(): Boolean
    fun popTo(key: K, inclusive: Boolean = false): Boolean
    fun remove(key: K)
    fun removeAll()
}

@Stable
interface NavigationStack<K, V> : NavigationBackStack<K> {
    fun push(key: K, content: (NavigationStackScope<K, V>) -> V)
}

//fun <K, V : K> NavigationStack<K, in V>.push(content: V) = push(content, content)

interface NavigationStackScope<K, V>: NavigationStack<K, V> {
    fun remove()
}