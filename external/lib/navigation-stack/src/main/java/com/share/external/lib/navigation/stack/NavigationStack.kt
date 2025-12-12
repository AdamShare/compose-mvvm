package com.share.external.lib.navigation.stack

import com.share.external.lib.view.ViewKey

/**
 * A navigation stack that supports pushing new views with forward navigation.
 *
 * Extends [NavigationBackStack] (which provides pop operations) with push capabilities,
 * enabling full navigation stack management.
 *
 * @param V The view type managed by this stack.
 */
interface NavigationStack<V>: NavigationBackStack {
    /**
     * Pushes a new [ViewKey] and [V] produced by [route]. If the key already exists it is
     * **replaced** and the old entry is cancelled after predictive-back animations complete.
     */
    fun push(route: NavigationRoute<V>)

    /**
     * Pushes a view using a factory that is also a [ViewKey].
     *
     * This is a convenience method for factories that implement both [ViewKey] and the
     * view creation function.
     *
     * @param factory The factory that provides both the key and view creation logic.
     */
    fun <T> push(factory: T) where T : ViewKey, T : (NavigationStackEntry<V>) -> V {
        push(NavigationRoute(factory) { factory.invoke(it) })
    }

    /**
     * Pushes a view using a [NavigationRouteFactory] with a dependency provider.
     *
     * @param factory The factory that creates the view.
     * @param dependency Function that extracts dependencies from the navigation entry.
     */
    fun <T, D> push(factory: T, dependency: (NavigationStackEntry<V>) -> D) where T : NavigationRouteFactory<D, V> {
        push(factory.toNavigationRoute(dependency))
    }
}
