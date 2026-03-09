package com.share.sample.feature.home

import com.getbackcompose.core.ViewProvider
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.stack.NavigationStack
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.navigation.stack.toNavigationRoute
import com.share.sample.core.data.model.Category
import com.share.sample.feature.details.DetailsGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for home feature.
 * Defines the lifetime boundary for home-scoped dependencies.
 */
object HomeScope

/**
 * Metro graph extension for the home feature.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(HomeScope::class) for per-instance caching.
 * Manages the home tab which displays the RSS feed browser with category selection and item list.
 */
@SingleIn(HomeScope::class)
@GraphExtension(scope = HomeScope::class)
interface HomeGraph {
    val viewProvider: HomeViewProvider
    val categoryFeedGraphFactory: CategoryFeedGraph.Factory
    val detailsGraphFactory: DetailsGraph.Factory

    /**
     * Dynamic dependencies for the Home feature.
     */
    class Dependency(val scope: ManagedCoroutineScope)

    /**
     * Factory for creating HomeGraph instances.
     * Function type for creating ViewProvider from scope.
     */
    @GraphExtension.Factory
    abstract class Factory : (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@Provides dependency: Dependency): HomeGraph

        /**
         * Creates a ViewProvider for the home feature.
         */
        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Dependency(scope)).viewProvider
        }
    }

    /**
     * Provider functions for home-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(HomeScope::class)
        @Provides
        fun categoryViewSwitcher(dependency: Dependency): HomeCategoryViewSwitcher =
            HomeCategoryViewSwitcher(scope = dependency.scope)

        @SingleIn(HomeScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            detailsGraphFactory: DetailsGraph.Factory,
            categoryFeedGraphFactory: CategoryFeedGraph.Factory,
            categoryViewSwitcher: HomeCategoryViewSwitcher,
        ): HomeViewProvider = HomeViewProvider(
            scope = dependency.scope,
            categoryViewSwitcher = categoryViewSwitcher,
            categoryFeedContent = { category, navigationStack, categoryScope ->
                categoryFeedGraphFactory(
                    CategoryFeedGraph.Dependency(
                        category = category,
                        navigationStack = navigationStack,
                        detailsRouteFactory = { feedItem ->
                            detailsGraphFactory.toNavigationRoute { navScope ->
                                DetailsGraph.Dependency(
                                    navigationScope = navScope,
                                    feedItem = feedItem,
                                    mediaType = category.mediaType
                                )
                            }
                        },
                    ),
                    categoryScope
                )
            },
        )
    }
}
