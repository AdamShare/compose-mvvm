package com.share.sample.feature.details.creator.viewall

import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.navigation.stack.toNavigationRoute
import com.share.sample.core.data.api.ArtistResult
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType
import com.share.sample.feature.details.DetailsGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for view all creator screen.
 * Defines the lifetime boundary for view all creator-scoped dependencies.
 */
object ViewAllCreatorScope

/**
 * Metro graph extension for the View All creator screen.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(ViewAllCreatorScope::class) for per-instance caching.
 * Shows a full-screen list of all items by a creator.
 */
@SingleIn(ViewAllCreatorScope::class)
@GraphExtension(scope = ViewAllCreatorScope::class)
interface ViewAllCreatorGraph {
    val viewProvider: ViewAllCreatorViewProvider

    /**
     * Dynamic dependencies for this view all creator instance.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val creator: Creator,
        val items: List<ArtistResult>,
        val mediaType: MediaType
    )

    /**
     * Factory for creating ViewAllCreatorGraph instances.
     * Implements NavigationRouteFactory for navigation.
     */
    @GraphExtension.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String get() = "ViewAllCreator"

        abstract fun create(@Provides dependency: Dependency): ViewAllCreatorGraph

        override fun create(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }

    /**
     * Provider functions for view all creator-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(ViewAllCreatorScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            detailsGraphFactory: DetailsGraph.Factory
        ): ViewAllCreatorViewProvider = ViewAllCreatorViewProvider(
            creator = dependency.creator,
            detailsRoute = { feedItem: FeedItem ->
                detailsGraphFactory.toNavigationRoute { scope ->
                    DetailsGraph.Dependency(
                        navigationScope = scope,
                        feedItem = feedItem,
                        mediaType = feedItem.mediaType
                    )
                }
            },
            items = dependency.items,
            navigationStack = dependency.navigationScope
        )
    }
}
