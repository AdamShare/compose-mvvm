package com.share.sample.feature.details

import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.navigation.stack.toNavigationRoute
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.Genre
import com.share.sample.core.data.model.MediaType
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.feature.details.creator.CreatorModalGraph
import com.share.sample.feature.details.genre.GenreGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for details feature.
 * Defines the lifetime boundary for details-scoped dependencies.
 */
object DetailsScope

/**
 * Metro graph extension for the details feature.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(DetailsScope::class) for per-instance caching.
 * Manages the item details screen, which can be reused from both
 * the Home and Favorites tabs.
 */
@SingleIn(DetailsScope::class)
@GraphExtension(scope = DetailsScope::class)
interface DetailsGraph {
    val viewProvider: DetailsViewProvider
    val creatorModalGraphFactory: CreatorModalGraph.Factory
    val genreGraphFactory: GenreGraph.Factory

    /**
     * Dynamic dependencies for this details instance.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val feedItem: FeedItem,
        val mediaType: MediaType
    )

    /**
     * Factory for creating DetailsGraph instances.
     * Implements NavigationRouteFactory for navigation.
     */
    @GraphExtension.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String = "Details"

        abstract fun create(@Provides dependency: Dependency): DetailsGraph

        override fun create(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }

    /**
     * Provider functions for details-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(DetailsScope::class)
        @Provides
        fun viewModel(
            dependency: Dependency,
            favoritesRepository: FavoritesRepository,
        ): DetailsViewModel = DetailsViewModel(
            scopeFactory = dependency.navigationScope,
            feedItem = dependency.feedItem,
            mediaType = dependency.mediaType,
            favoritesRepository = favoritesRepository,
        )

        @SingleIn(DetailsScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            creatorModalGraphFactory: CreatorModalGraph.Factory,
            genreGraphFactory: GenreGraph.Factory,
            viewModel: DetailsViewModel,
        ): DetailsViewProvider = DetailsViewProvider(
            navigationStack = dependency.navigationScope,
            feedItem = dependency.feedItem,
            creatorModalRoute = { creator: Creator, mediaType: MediaType ->
                creatorModalGraphFactory.toNavigationRoute { scope ->
                    CreatorModalGraph.Dependency(
                        navigationScope = scope,
                        creator = creator,
                        mediaType = mediaType,
                    )
                }
            },
            genreRoute = { genre: Genre ->
                genreGraphFactory.toNavigationRoute { scope ->
                    GenreGraph.Dependency(
                        navigationScope = scope,
                        genre = genre
                    )
                }
            },
            viewModel = viewModel,
        )
    }
}
