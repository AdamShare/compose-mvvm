package com.share.external.lib.mvvm.navigation.stack

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycle
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycleScope

/**
 * Entry‑level navigation API used by feature modules to display new screens.
 *
 * @param V The type produced by the [content] factory, typically a
 *          `ComposableProvider` or another view abstraction.
 */
interface NavigationStackScope<V>: NavigationBackStack, ViewLifecycle, ManagedCoroutineScope {
    /** Pushes a new [NavigationKey] and returns the value produced by
     *  [content]. If the key already exists it is **replaced** and the old
     *  entry is cancelled after predictive‑back animations complete.
     */
    fun push(key: NavigationKey, content: (NavigationStackEntry<V>) -> V)

    fun <T> push(content: T) where T: NavigationKey, T: (NavigationStackEntry<V>) -> V {
        push(content, content)
    }
}

interface NavigationViewFactory<V>: (NavigationStackEntry<V>) -> V, NavigationKey

internal open class NavigationStackContext<V>(
    private val scope: ManagedCoroutineScope,
    private val stack: ViewModelNavigationStack<V>,
): ViewLifecycleScope(scope), NavigationBackStack by stack, NavigationStackScope<V> {
    override fun push(key: NavigationKey, content: (NavigationStackEntry<V>) -> V) {
        val context = NavigationStackEntryContext(
            key = key,
            scope = scope.childManagedScope(key.analyticsId),
            stack = stack
        )
        stack.push(
            key = key,
            content = content(context),
            scope = context
        )
    }
}
