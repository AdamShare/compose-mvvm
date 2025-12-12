package com.share.external.lib.navigation.stack

import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewKey

/**
 * Entry-level navigation API used by feature modules to display new screens.
 */
interface NavigationStackScope<V> : NavigationStack<V>, ManagedCoroutineScope

internal open class NavigationStackScopeImpl<V, E: ManagedCancellable>(
    scope: ManagedCoroutineScope,
    private val stack: ManagedCoroutineScopeStack<V, E>,
) : ManagedCoroutineScope by scope,
    NavigationBackStack by stack,
    NavigationStackScope<V> {
    override fun push(route: NavigationRoute<V>) {
        val key = route.key
        val context = NavigationStackEntryImpl(
            key = key,
            scope = childManagedScope(key.name),
            stack = stack
        )
        stack.push(
            key = key,
            scope = context,
            viewProvider = route.factory(context),
        )
    }
}
