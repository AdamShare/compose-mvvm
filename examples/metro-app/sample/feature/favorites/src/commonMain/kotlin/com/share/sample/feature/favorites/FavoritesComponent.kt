package com.share.sample.feature.favorites

import com.getbackcompose.core.ViewProvider
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.stack.NavigationRoute
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.navigation.stack.toNavigationRoute
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository
import com.share.sample.feature.details.DetailsGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for favorites feature.
 * Defines the lifetime boundary for favorites-scoped dependencies.
 */
object FavoritesScope

/**
 * Metro graph extension for the favorites feature.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(FavoritesScope::class) for per-instance caching.
 */
@SingleIn(FavoritesScope::class)
@GraphExtension(scope = FavoritesScope::class)
interface FavoritesGraph {
    val viewProvider: FavoritesViewProvider
    val detailsGraphFactory: DetailsGraph.Factory

    /**
     * Dynamic dependencies for the Favorites feature.
     */
    class Dependency(val scope: ManagedCoroutineScope)

    /**
     * Factory for creating FavoritesGraph instances.
     * Function type for creating ViewProvider from scope.
     */
    @GraphExtension.Factory
    abstract class Factory : (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@Provides dependency: Dependency): FavoritesGraph

        /**
         * Creates a ViewProvider for the favorites feature.
         */
        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Dependency(scope)).viewProvider
        }
    }

    /**
     * Provider functions for favorites-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(FavoritesScope::class)
        @Provides
        fun viewModel(
            dependency: Dependency,
            favoritesRepository: FavoritesRepository,
            feedRepository: FeedRepository,
        ): FavoritesViewModel = FavoritesViewModel(
            favoritesRepository = favoritesRepository,
            feedRepository = feedRepository,
            scopeFactory = dependency.scope,
        )

        @SingleIn(FavoritesScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            detailsGraphFactory: DetailsGraph.Factory,
            viewModel: FavoritesViewModel,
        ): FavoritesViewProvider = FavoritesViewProvider(
            scope = dependency.scope,
            detailsRoute = { feedItem: FeedItem, mediaType: MediaType ->
                detailsGraphFactory.toNavigationRoute { scope ->
                    DetailsGraph.Dependency(
                        navigationScope = scope,
                        feedItem = feedItem,
                        mediaType = mediaType
                    )
                }
            },
            viewModel = viewModel,
        )
    }
}
