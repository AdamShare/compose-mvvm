package com.share.sample.feature.home

import com.getbackcompose.core.ViewProvider
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.stack.NavigationRoute
import com.getbackcompose.navigation.stack.NavigationStack
import com.getbackcompose.navigation.stack.Screen
import com.share.sample.core.data.model.Category
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.repository.FeedRepository
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for category feed.
 * Defines the lifetime boundary for category feed-scoped dependencies.
 */
object CategoryFeedScope

/**
 * Metro graph extension for the category feed.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(CategoryFeedScope::class) for per-instance caching.
 */
@SingleIn(CategoryFeedScope::class)
@GraphExtension(scope = CategoryFeedScope::class)
interface CategoryFeedGraph {
    val viewProvider: CategoryFeedViewProvider

    /**
     * Dynamic dependencies for the category feed.
     */
    class Dependency(
        val category: Category,
        val navigationStack: NavigationStack<Screen>,
        val detailsRouteFactory: (FeedItem) -> NavigationRoute<Screen>,
    )

    /**
     * Factory for creating CategoryFeedGraph instances.
     * Function type with two parameters: Dependency and ManagedCoroutineScope.
     */
    @GraphExtension.Factory
    abstract class Factory : (Dependency, ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(
            @Provides dependency: Dependency,
            @Provides scope: ManagedCoroutineScope,
        ): CategoryFeedGraph

        override fun invoke(dependency: Dependency, scope: ManagedCoroutineScope): ViewProvider {
            return create(dependency, scope).viewProvider
        }
    }

    /**
     * Provider functions for category feed-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(CategoryFeedScope::class)
        @Provides
        fun viewModel(
            dependency: Dependency,
            scope: ManagedCoroutineScope,
            feedRepository: FeedRepository,
        ): CategoryFeedViewModel = CategoryFeedViewModel(
            scopeFactory = scope,
            category = dependency.category,
            feedRepository = feedRepository,
        )

        @SingleIn(CategoryFeedScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            viewModel: CategoryFeedViewModel,
        ): CategoryFeedViewProvider = CategoryFeedViewProvider(
            navigationStack = dependency.navigationStack,
            detailsRouteFactory = dependency.detailsRouteFactory,
            viewModel = viewModel,
        )
    }
}
