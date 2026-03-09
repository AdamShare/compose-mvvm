package com.getbackcompose.navigation.stack

import com.getbackcompose.core.ViewKey

interface NavigationRoute<V> {
    val factory: (NavigationStackEntry<V>) -> V
    val key: ViewKey
}

fun <V> NavigationRoute(
    key: ViewKey,
    factory: (NavigationStackEntry<V>) -> V,
): NavigationRoute<V> = NavigationRouteImpl(
    factory = factory,
    key = key
)

internal data class NavigationRouteImpl<V>(
    override val factory: (NavigationStackEntry<V>) -> V,
    override val key: ViewKey,
): NavigationRoute<V>
