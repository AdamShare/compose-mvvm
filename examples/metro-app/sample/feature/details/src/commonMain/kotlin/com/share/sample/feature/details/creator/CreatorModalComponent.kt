package com.share.sample.feature.details.creator

import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.navigation.stack.toNavigationRoute
import com.share.sample.core.data.api.ArtistResult
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType
import com.share.sample.core.data.repository.FeedRepository
import com.share.sample.feature.details.DetailsGraph
import com.share.sample.feature.details.creator.viewall.ViewAllCreatorGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for creator modal.
 * Defines the lifetime boundary for creator modal-scoped dependencies.
 */
object CreatorModalScope

/**
 * Metro graph extension for the creator modal.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(CreatorModalScope::class) for per-instance caching.
 */
@SingleIn(CreatorModalScope::class)
@GraphExtension(scope = CreatorModalScope::class)
interface CreatorModalGraph {
    val viewProvider: CreatorModalViewProvider
    val viewAllCreatorGraphFactory: ViewAllCreatorGraph.Factory

    /**
     * Dynamic dependencies for this creator modal instance.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val creator: Creator,
        val mediaType: MediaType,
    )

    /**
     * Factory for creating CreatorModalGraph instances.
     * Implements NavigationRouteFactory for navigation.
     */
    @GraphExtension.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String get() = "CreatorModal"

        abstract fun create(@Provides dependency: Dependency): CreatorModalGraph

        override fun invoke(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }

    /**
     * Provider functions for creator modal-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(CreatorModalScope::class)
        @Provides
        fun viewModel(
            dependency: Dependency,
            feedRepository: FeedRepository
        ): CreatorModalViewModel = CreatorModalViewModel(
            scopeFactory = dependency.navigationScope,
            creator = dependency.creator,
            mediaType = dependency.mediaType,
            feedRepository = feedRepository
        )

        @SingleIn(CreatorModalScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            detailsGraphFactory: DetailsGraph.Factory,
            viewAllCreatorGraphFactory: ViewAllCreatorGraph.Factory,
            viewModel: CreatorModalViewModel
        ): CreatorModalViewProvider = CreatorModalViewProvider(
            detailsRoute = { feedItem: FeedItem, itemMediaType: MediaType ->
                detailsGraphFactory.toNavigationRoute { scope ->
                    DetailsGraph.Dependency(
                        navigationScope = scope,
                        feedItem = feedItem,
                        mediaType = itemMediaType
                    )
                }
            },
            navigationStack = dependency.navigationScope,
            viewModel = viewModel,
            viewAllRoute = { viewAllCreator: Creator, items: List<ArtistResult>, viewAllMediaType: MediaType ->
                viewAllCreatorGraphFactory.toNavigationRoute { scope ->
                    ViewAllCreatorGraph.Dependency(
                        navigationScope = scope,
                        creator = viewAllCreator,
                        items = items,
                        mediaType = viewAllMediaType
                    )
                }
            }
        )
    }
}
