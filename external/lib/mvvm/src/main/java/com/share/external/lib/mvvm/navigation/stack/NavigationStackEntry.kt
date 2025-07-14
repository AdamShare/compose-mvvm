package com.share.external.lib.mvvm.navigation.stack

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.mvvm.navigation.content.ViewProvider

/**
 * A navigation context bound to one entry inside the parent stack. Lets the hosted screen push additional content or
 * manipulate the stack relative to its own position.
 */
interface NavigationStackEntry<V> : NavigationStackScope<V> {
    /** Removes this entry regardless of its position. */
    fun remove()

    /** Pops the stack until [key] is at the top. */
    fun popUpTo(inclusive: Boolean = false)
}

internal class NavigationStackEntryContext<V>(
    private val key: NavigationKey,
    scope: ManagedCoroutineScope,
    private val stack: ViewModelNavigationStack<V>,
) : NavigationStackContext<V>(scope = scope, stack = stack),
    NavigationStackEntry<V>
        where V: ViewProvider, V: ViewPresentation {
    override fun remove() {
        stack.remove(key = key)
    }

    override fun popUpTo(inclusive: Boolean) {
        stack.popTo(key = key, inclusive = inclusive)
    }
}
