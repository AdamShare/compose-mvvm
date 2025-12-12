package com.share.sample.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.share.external.lib.compose.runtime.StateProvider
import com.share.external.lib.navigation.switcher.ViewSwitcher
import com.share.external.lib.navigation.switcher.ViewSwitcherContent
import com.share.external.lib.navigation.switcher.ViewSwitcherHost
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.feature.favorites.FavoritesComponent
import com.share.sample.feature.home.HomeComponent
import com.share.sample.feature.profile.ProfileComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object MainTabViewModule {
    @MainTabScope
    @Provides
    fun viewSwitcher(scope: MainTabComponent.Scope) = MainTabViewSwitcher(scope = scope)

    @MainTabScope
    @Provides
    fun mainTabViewProvider(
        favorites: FavoritesComponent.Factory,
        home: HomeComponent.Factory,
        profile: ProfileComponent.Factory,
        viewSwitcher: MainTabViewSwitcher,
    ) = MainTabViewProvider(
        viewSwitcher = viewSwitcher
    ) { route, tabScope ->
        when (route) {
            TabRoute.Favorites -> favorites(tabScope)
            TabRoute.Home -> home(tabScope)
            TabRoute.Profile -> profile(tabScope)
        }
    }
}

class MainTabViewProvider(
    private val viewSwitcher: MainTabViewSwitcher,
    private val content: ViewSwitcherContent<TabRoute>,
) : ViewProvider {
    override fun onViewAppear(scope: CoroutineScope) = MainTabView(
        viewSwitcher = viewSwitcher,
        tabBarSwitcherContent = content,
        scope = scope
    )
}

class MainTabView(
    private val viewSwitcher: ViewSwitcher<TabRoute>,
    private val tabBarSwitcherContent: ViewSwitcherContent<TabRoute>,
    override val scope: CoroutineScope,
) : View, StateProvider {
    override val content: @Composable () -> Unit = {
        val currentRoute = viewSwitcher.selected

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                MainTabNavigationBar(
                    currentRoute = currentRoute,
                    onTabSelected = { viewSwitcher.onSelect(it) }
                )
            }
        ) { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)) {
                ViewSwitcherHost(
                    switcher = viewSwitcher,
                    content = tabBarSwitcherContent
                )
            }
        }
    }
}

@Composable
private fun MainTabNavigationBar(
    currentRoute: TabRoute?,
    onTabSelected: (TabRoute) -> Unit
) {
    NavigationBar {
        TabRoute.entries.forEach { route ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        imageVector = route.icon(selected = currentRoute == route),
                        contentDescription = route.name
                    )
                },
                label = { Text(text = route.name) }
            )
        }
    }
}

private fun TabRoute.icon(selected: Boolean): ImageVector = when (this) {
    TabRoute.Home -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
    TabRoute.Favorites -> if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
    TabRoute.Profile -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
}
