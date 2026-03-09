package com.share.sample.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.getbackcompose.compose.runtime.StateProvider
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewKey
import com.getbackcompose.core.ViewProvider
import com.getbackcompose.core.VisibilityScopedView
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.switcher.RetainingScopeViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcherContent
import com.getbackcompose.navigation.switcher.ViewSwitcherHost
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.AuthState
import com.share.sample.core.auth.InMemoryCredentialsStorage
import com.share.sample.core.data.api.AppleRssClient
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository
import com.share.sample.core.data.repository.InMemoryFavoritesStorage
import com.share.sample.feature.main.MainTabViewProvider
import com.share.sample.feature.onboarding.OnboardingViewProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun main() = application {
    val authRepository = AuthRepository(InMemoryCredentialsStorage())
    val feedRepository = FeedRepository(AppleRssClient())
    val favoritesRepository = FavoritesRepository(InMemoryFavoritesStorage())

    Window(
        onCloseRequest = ::exitApplication,
        title = "GetBack Sample"
    ) {
        val view = VisibilityScopedView(
            scopeFactory = { CoroutineScope(SupervisorJob() + Dispatchers.Main) },
            viewProvider = DesktopMainViewProvider(
                authRepository = authRepository,
                feedRepository = feedRepository,
                favoritesRepository = favoritesRepository,
            )
        )
        view.content()
    }
}

private enum class AppRoute : ViewKey {
    Onboarding,
    Main
}

private class DesktopMainViewProvider(
    private val authRepository: AuthRepository,
    private val feedRepository: FeedRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewProvider {
    override fun onViewAppear(scope: CoroutineScope): View {
        val managedScope = ManagedCoroutineScope(actual = scope)

        val viewSwitcher = RetainingScopeViewSwitcher(
            scope = managedScope,
            defaultKey = AppRoute.Onboarding
        )

        scope.launch {
            authRepository.authState.collect { state ->
                val newRoute = when (state) {
                    is AuthState.LoggedIn -> AppRoute.Main
                    AuthState.LoggedOut -> AppRoute.Onboarding
                }
                viewSwitcher.onSelect(newRoute)
            }
        }

        return DesktopMainView(
            viewSwitcher = viewSwitcher,
            authRepository = authRepository,
            feedRepository = feedRepository,
            favoritesRepository = favoritesRepository,
            scope = scope
        )
    }
}

private class DesktopMainView(
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
