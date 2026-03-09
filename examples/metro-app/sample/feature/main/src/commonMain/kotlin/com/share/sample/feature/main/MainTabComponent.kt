package com.share.sample.feature.main

import com.getbackcompose.core.ViewProvider
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.share.sample.feature.favorites.FavoritesGraph
import com.share.sample.feature.home.HomeGraph
import com.share.sample.feature.profile.ProfileGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for main tab container.
 * Defines the lifetime boundary for main tab-scoped dependencies.
 */
object MainTabScope

/**
 * Metro graph extension for the main tab container.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(MainTabScope::class) for per-instance caching.
 * Manages the tab-based navigation when the user is logged in.
 */
@SingleIn(MainTabScope::class)
@GraphExtension(scope = MainTabScope::class)
interface MainTabGraph {
    val viewProvider: MainTabViewProvider

    val favorites: FavoritesGraph.Factory
    val home: HomeGraph.Factory
    val profile: ProfileGraph.Factory

    /**
     * Scope wrapper for the MainTab coroutine scope.
     */
    class Dependency(val scope: ManagedCoroutineScope)

    /**
     * Factory for creating MainTabGraph instances.
     * Function type for creating ViewProvider from scope.
     */
    @GraphExtension.Factory
    abstract class Factory : (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@Provides dependency: Dependency): MainTabGraph

        /**
         * Creates a ViewProvider for the main tab container.
         */
        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Dependency(scope)).viewProvider
        }
    }

    /**
     * Provider functions for main tab-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(MainTabScope::class)
        @Provides
        fun viewSwitcher(dependency: Dependency): MainTabViewSwitcher = MainTabViewSwitcher(scope = dependency.scope)
        
        @SingleIn(MainTabScope::class)
        @Provides
        fun viewProvider(
            favorites: FavoritesGraph.Factory,
            home: HomeGraph.Factory,
            profile: ProfileGraph.Factory,
            viewSwitcher: MainTabViewSwitcher,
        ): MainTabViewProvider = MainTabViewProvider(
            viewSwitcher = viewSwitcher
        ) { route, tabScope ->
            when (route) {
                TabRoute.Favorites -> favorites(tabScope)
                TabRoute.Home -> home(tabScope)
                TabRoute.Profile -> profile(tabScope)
            }
        }
    }
}
