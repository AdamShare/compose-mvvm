package com.getbackcompose.navigation.stack

import com.getbackcompose.core.ViewKey

/**
 * A factory for creating navigation views from a [NavigationStackEntry].
 *
 * This interface binds a [ViewKey] to a view creation function, enabling dynamic instantiation of views when
 * navigating within a [ManagedCoroutineScopeStack].
 *
 * @param V The type of view to produce.
 */
interface NavigationRouteFactory<D, V> : ViewKey {
    fun create(dependency: D): V
}

fun <D, V> NavigationRouteFactory<D, V>.toNavigationRoute(
    dependency: (NavigationStackEntry<V>) -> D
) = NavigationRoute(
    key = this,
    factory = {
        create(dependency(it))
    },
)

fun <V> NavigationRouteFactory<NavigationStackEntry<V>, V>.toNavigationRoute() = NavigationRoute(
    key = this,
    factory = { create(it) },
)
