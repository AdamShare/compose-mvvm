package com.share.sample.feature.home

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.view.ViewProvider
import com.share.sample.core.data.model.Category
import com.share.sample.core.data.model.FeedItem
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class CategoryFeedScope

@CategoryFeedScope
@Subcomponent(modules = [CategoryFeedModule::class])
interface CategoryFeedComponent {
    val viewProvider: CategoryFeedViewProvider

    class Dependency(
        val category: Category,
        val navigationStack: NavigationStack<Screen>,
        val detailsRouteFactory: (FeedItem) -> NavigationRoute<Screen>,
    )

    @Subcomponent.Factory
    abstract class Factory : (Dependency, ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(
            @BindsInstance dependency: Dependency,
            @BindsInstance scope: ManagedCoroutineScope,
        ): CategoryFeedComponent

        override fun invoke(dependency: Dependency, scope: ManagedCoroutineScope): ViewProvider {
            return create(dependency, scope).viewProvider
        }
    }
}

@Module
object CategoryFeedModule {
    @CategoryFeedScope
    @Provides
    fun viewModel(
        dependency: CategoryFeedComponent.Dependency,
        scope: ManagedCoroutineScope,
        feedRepository: com.share.sample.core.data.repository.FeedRepository,
    ) = CategoryFeedViewModel(
        scopeFactory = scope,
        category = dependency.category,
        feedRepository = feedRepository,
    )

    @CategoryFeedScope
    @Provides
    fun viewProvider(
        dependency: CategoryFeedComponent.Dependency,
        viewModel: CategoryFeedViewModel,
    ) = CategoryFeedViewProvider(
        navigationStack = dependency.navigationStack,
        detailsRouteFactory = dependency.detailsRouteFactory,
        viewModel = viewModel,
    )
}
