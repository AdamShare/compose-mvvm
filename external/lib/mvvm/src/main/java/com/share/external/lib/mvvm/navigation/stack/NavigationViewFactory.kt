package com.share.external.lib.mvvm.navigation.stack

import com.share.external.lib.mvvm.navigation.content.NavigationKey

/**
 * A factory for creating navigation views from a [NavigationStackEntry].
 *
 * This interface binds a [NavigationKey] to a view creation function, enabling dynamic instantiation of views when
 * navigating within a [ManagedCoroutineScopeStack].
 *
 * @param V The type of view to produce.
 */
interface NavigationViewFactory<V> : (NavigationStackEntry<V>) -> V, NavigationKey
