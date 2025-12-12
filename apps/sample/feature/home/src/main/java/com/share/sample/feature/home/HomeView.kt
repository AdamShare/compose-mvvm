package com.share.sample.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.compose.runtime.StateProvider
import com.share.external.lib.navigation.stack.ModalNavigationStack
import com.share.external.lib.navigation.stack.NavigationStack
import com.share.external.lib.navigation.stack.NavigationStackHost
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.navigation.stack.toNavigationRoute
import com.share.external.lib.navigation.switcher.ViewSwitcher
import com.share.external.lib.navigation.switcher.ViewSwitcherContent
import com.share.external.lib.navigation.switcher.ViewSwitcherHost
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.core.data.model.Category
import com.share.sample.feature.details.DetailsComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object HomeViewModule {
    @HomeScope
    @Provides
    fun categoryViewSwitcher(dependency: HomeComponent.Dependency) =
        HomeCategoryViewSwitcher(scope = dependency.scope)

    @HomeScope
    @Provides
    fun homeViewProvider(
        dependency: HomeComponent.Dependency,
        detailsFactory: DetailsComponent.Factory,
        categoryFeedFactory: CategoryFeedComponent.Factory,
        categoryViewSwitcher: HomeCategoryViewSwitcher,
    ) = HomeViewProvider(
        scope = dependency.scope,
        categoryViewSwitcher = categoryViewSwitcher,
        categoryFeedContent = { category, navigationStack, categoryScope ->
            categoryFeedFactory(
                CategoryFeedComponent.Dependency(
                    category = category,
                    navigationStack = navigationStack,
                    detailsRouteFactory = { feedItem ->
                        detailsFactory.toNavigationRoute { navScope ->
                            DetailsComponent.Dependency(
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

class HomeViewProvider(
    scope: ManagedCoroutineScope,
    private val categoryViewSwitcher: HomeCategoryViewSwitcher,
    private val categoryFeedContent: (Category, NavigationStack<Screen>, ManagedCoroutineScope) -> ViewProvider,
) : Screen {
    private val navigationStack = ModalNavigationStack<Screen>(rootScope = scope)
    private val rootNavigationScope = navigationStack.rootNavigationScope()

    private val categoryContent = ViewSwitcherContent<Category> { category, categoryScope ->
        categoryFeedContent(category, rootNavigationScope, categoryScope)
    }

    override fun onViewAppear(scope: CoroutineScope) = HomeView(
        categoryViewSwitcher = categoryViewSwitcher,
        categoryContent = categoryContent,
        navigationStack = navigationStack,
        scope = scope
    )
}

class HomeView(
    private val categoryViewSwitcher: ViewSwitcher<Category>,
    private val categoryContent: ViewSwitcherContent<Category>,
    private val navigationStack: ModalNavigationStack<Screen>,
    override val scope: CoroutineScope,
) : View, StateProvider {
    override val content: @Composable () -> Unit = {
        NavigationStackHost(
            name = "HomeNavigationStackHost",
            stack = navigationStack,
        ) {
            HomeScreenContent(
                selectedCategory = categoryViewSwitcher.selected ?: Category.default,
                categories = Category.entries,
                onCategorySelected = { categoryViewSwitcher.onSelect(it) },
                categoryViewSwitcher = categoryViewSwitcher,
                categoryContent = categoryContent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    selectedCategory: Category,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    categoryViewSwitcher: ViewSwitcher<Category>,
    categoryContent: ViewSwitcherContent<Category>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Browse") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category chips
            CategorySelector(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )

            // Content for selected category - each category has its own retained view
            ViewSwitcherHost(
                switcher = categoryViewSwitcher,
                content = categoryContent
            )
        }
    }
}

@Composable
private fun CategorySelector(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(text = category.displayName) }
            )
        }
    }
}
