package com.share.external.lib.mvvm.navigation.stack

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey

/**
 * A navigation context bound to one entry inside the parent stack. Lets the
 * hosted screen push additional content or manipulate the stack relative to
 * its own position.
 */
interface NavigationStackEntry<V>: NavigationStack<V>, ManagedCoroutineScope {
    /** Removes this entry regardless of its position. */
    fun remove()
    /** Pops the stack until [key] is at the top. */
    fun popUpTo(inclusive: Boolean = false)
}

class NavigationContext<V>(
    private val key: NavigationKey,
    scope: ManagedCoroutineScope,
    private val stack: ViewModelNavigationStack<V>,
): RootNavigationContext<V>(
    scope = scope,
    stack = stack
), NavigationStackEntry<V> {
    override fun remove() {
        stack.remove(key = key)
    }

    override fun popUpTo(inclusive: Boolean) {
        stack.popTo(
            key = key,
            inclusive = inclusive
        )
    }
}
