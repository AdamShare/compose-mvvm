package com.share.sample.integrations.main

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.compose.runtime.LoggingStateChangeObserver
import com.getbackcompose.compose.runtime.StateProvider
import com.getbackcompose.navigation.switcher.ViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcherHost
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewProvider
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.AuthState
import com.share.sample.feature.main.MainTabGraph
import com.share.sample.feature.onboarding.OnboardingGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainViewProvider(
    private val authRepository: AuthRepository,
    private val mainTabFactory: MainTabGraph.Factory,
    private val onboardingFactory: OnboardingGraph.Factory,
    override val scope: CoroutineScope,
    private val viewSwitcher: ViewSwitcher<ActivityViewRoute>,
) : ViewProvider, StateProvider {
    init {
        scope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.LoggedIn -> {
                        viewSwitcher.onSelect(ActivityViewRoute.LoggedIn)
                    }
                    AuthState.LoggedOut -> {
                        viewSwitcher.onSelect(ActivityViewRoute.LoggedOut)
                    }
                }
            }
        }
    }

    override fun onViewAppear(scope: CoroutineScope) =
        MainView(
            viewSwitcher = viewSwitcher,
            onboarding = onboardingFactory,
            tab = mainTabFactory,
            scope = scope
        )
}

class MainView(
    private val onboarding: (ManagedCoroutineScope) -> ViewProvider,
    private val tab: (ManagedCoroutineScope) -> ViewProvider,
    override val scope: CoroutineScope,
    private val viewSwitcher: ViewSwitcher<ActivityViewRoute>,
) : View, StateProvider, LoggingStateChangeObserver {

    override val content: @Composable () -> Unit = {
        MaterialTheme(colorScheme = darkColorScheme()) {
            ViewSwitcherHost(switcher = viewSwitcher) { route, switcherScope ->
                when (route) {
                    ActivityViewRoute.LoggedIn -> tab(switcherScope)
                    ActivityViewRoute.LoggedOut -> onboarding(switcherScope)
                }
            }
        }
    }
}
