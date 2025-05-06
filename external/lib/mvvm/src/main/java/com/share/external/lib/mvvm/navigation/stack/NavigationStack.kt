package com.share.external.lib.mvvm.navigation.stack

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey

/**
 * Entry‑level navigation API used by feature modules to display new screens.
 *
 * @param V The type produced by the [content] factory, typically a
 *          `ComposableProvider` or another view abstraction.
 */
interface NavigationStack<V>: NavigationBackStack {
    /** Pushes a new [NavigationKey] and returns the value produced by
     *  [content]. If the key already exists it is **replaced** and the old
     *  entry is cancelled after predictive‑back animations complete.
     */
    fun push(key: NavigationKey, content: (NavigationContext<V>) -> V)
}

open class RootNavigationContext<V>(
    private val scope: ManagedCoroutineScope,
    private val stack: ViewModelNavigationStack<V>,
): ManagedCoroutineScope by scope, NavigationBackStack by stack, NavigationStack<V> {
    override fun push(key: NavigationKey, content: (NavigationContext<V>) -> V) {
        val routeScope = scope.childManagedScope(key.analyticsId)
        stack.push(
            key = key,
            content = content(
                NavigationContext(
                    key = key,
                    scope = routeScope,
                    stack = stack
                )
            ),
            scope = routeScope
        )
    }
}
