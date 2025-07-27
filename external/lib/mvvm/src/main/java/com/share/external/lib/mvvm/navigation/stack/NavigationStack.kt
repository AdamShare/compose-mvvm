package com.share.external.lib.mvvm.navigation.stack

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.core.ViewProvider

interface NavigationStack<V>: NavigationBackStack {
    /**
     * Pushes a new [NavigationKey] and returns the value produced by [content]. If the key already exists it is
     * **replaced** and the old entry is cancelled after predictive‑back animations complete.
     */
    fun push(key: NavigationKey, content: (NavigationStackEntry<V>) -> V)

    fun <T> push(content: T) where T : NavigationKey, T : (NavigationStackEntry<V>) -> V {
        push(key = content, content = content)
    }
}

/**
 * Entry‑level navigation API used by feature modules to display new screens.
 */
interface NavigationStackScope<V> : NavigationStack<V>, ManagedCoroutineScope

internal open class NavigationStackContext<V>(
    scope: ManagedCoroutineScope,
    private val stack: ViewModelNavigationStack<V>,
) : ManagedCoroutineScope by scope,
    NavigationBackStack by stack,
    NavigationStackScope<V> where V: ViewProvider, V: ViewPresentation {
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
