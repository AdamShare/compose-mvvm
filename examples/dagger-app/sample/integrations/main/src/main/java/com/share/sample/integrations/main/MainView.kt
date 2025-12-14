package com.share.sample.integrations.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.compose.runtime.LoggingStateChangeObserver
import com.getbackcompose.compose.runtime.StateProvider
import com.getbackcompose.navigation.switcher.ViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcherHost
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewProvider
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.AuthState
import com.share.sample.feature.main.MainTabComponent
import com.share.sample.feature.onboarding.OnboardingComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Module(
    subcomponents = [
        OnboardingComponent::class,
        MainTabComponent::class,
    ]
)
object MainViewModule {
    @MainViewScope
    @Provides
    fun mainViewSwitcher(scope: MainViewProviderScope): MainViewSwitcher = MainViewSwitcher(scope)

    @MainViewScope
    @Provides
    fun mainViewProvider(
        authRepository: AuthRepository,
        mainTab: MainTabComponent.Factory,
        onboarding: OnboardingComponent.Factory,
        scope: MainViewProviderScope,
        viewSwitcher: MainViewSwitcher
    ): MainViewProvider = MainViewProvider(
        authRepository = authRepository,
        tab = mainTab,
        onboarding = onboarding,
        scope = scope.create("MainView"),
        viewSwitcher = viewSwitcher
    )
}

class MainViewProvider(
    private val authRepository: AuthRepository,
    private val tab: MainTabComponent.Factory,
    private val onboarding: OnboardingComponent.Factory,
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
            onboarding = onboarding,
            tab = tab,
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
