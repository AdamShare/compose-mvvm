package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.ComposableProvider
import com.share.external.lib.mvvm.navigation.NavStackEntry

@Stable
interface NavigationBackStack {
    val size: Int
    fun pop(): Boolean
    fun popTo(entry: NavStackEntry<*, *>, inclusive: Boolean = false): Boolean
    fun remove(entry: NavStackEntry<*, *>)
    fun removeAll()
}

@Stable
interface NavigationStack : NavigationBackStack {
    fun push(entry: NavStackEntry<*, *>, transaction: NavigationStack.() -> Unit = {})
}

//fun <K, V : K> NavigationStack<K, in V>.push(content: V) = push(content, content)

interface NavigationStackScope<K, V>: NavigationStack<K, V> {
    fun remove()
}
