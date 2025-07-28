package com.share.external.lib.mvvm.navigation.stack

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation

/**
 * Entry‑level navigation API used by feature modules to display new screens.
 */
interface NavigationStackScope<V> : NavigationStack<V>, ManagedCoroutineScope

internal open class NavigationStackContext<V, E: ManagedCoroutineScope>(
    scope: ManagedCoroutineScope,
    private val stack: ManagedCoroutineScopeStack<V, E>,
) : ManagedCoroutineScope by scope,
    NavigationBackStack by stack,
    NavigationStackScope<V> {
    override fun push(key: NavigationKey, content: (NavigationStackEntry<V>) -> V) {
        val context = NavigationStackEntryContext(
            key = key,
            scope = childManagedScope(key.name),
            stack = stack
        )
        stack.push(
            key = key,
            scope = context,
            viewProvider = content(context),
        )
    }
}
