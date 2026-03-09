package com.share.sample.app

import androidx.compose.runtime.Composable
import com.getbackcompose.compose.runtime.StateProvider
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewKey
import com.getbackcompose.core.ViewProvider
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.switcher.RetainingScopeViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcherContent
import com.getbackcompose.navigation.switcher.ViewSwitcherHost
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.AuthState
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository
import com.share.sample.feature.main.MainTabViewProvider
import com.share.sample.feature.onboarding.OnboardingViewProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Sealed class representing the main app routes.
 */
enum class AppRoute : ViewKey {
    Onboarding,
    Main
}

/**
 * Main application ViewProvider that manages auth-based navigation.
 */
class MainViewProvider(
    private val authRepository: AuthRepository,
    private val feedRepository: FeedRepository,
    private val favoritesRepository: FavoritesRepository,
    managedScope: ManagedCoroutineScope
) : ViewProvider {
    private val viewSwitcher = RetainingScopeViewSwitcher(
        scope = managedScope,
        defaultKey = AppRoute.Onboarding
    )

    private val internalScope = managedScope.create("MainViewProvider")

    init {
        // Automatically switch based on auth state
        internalScope.launch {
            authRepository.authState.collect { state ->
                val newRoute = when (state) {
                    is AuthState.LoggedIn -> AppRoute.Main
                    AuthState.LoggedOut -> AppRoute.Onboarding
                }
                viewSwitcher.onSelect(newRoute)
            }
        }
    }

    override fun onViewAppear(scope: CoroutineScope): View {
        return MainView(
            viewSwitcher = viewSwitcher,
            authRepository = authRepository,
            feedRepository = feedRepository,
            favoritesRepository = favoritesRepository,
            scope = scope
        )
    }
}

/**
 * Main application View that switches between onboarding and main content.
 */
class MainView(
    private val viewSwitcher: ViewSwitcher<AppRoute>,
    private val authRepository: AuthRepository,
    private val feedRepository: FeedRepository,
    private val favoritesRepository: FavoritesRepository,
    override val scope: CoroutineScope,
) : View, StateProvider {
    override val content: @Composable () -> Unit = {
        val switcherContent = ViewSwitcherContent<AppRoute> { route, routeScope ->
            when (route) {
                AppRoute.Main -> MainTabViewProvider(
                    scope = routeScope,
                    authRepository = authRepository,
                    feedRepository = feedRepository,
                    favoritesRepository = favoritesRepository
                )
                AppRoute.Onboarding -> OnboardingViewProvider(
                    scope = routeScope,
                    authRepository = authRepository
                )
            }
        }

        ViewSwitcherHost(
            switcher = viewSwitcher,
            content = switcherContent
        )
    }
}
