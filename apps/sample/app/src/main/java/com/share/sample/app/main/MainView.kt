package com.share.sample.app.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.compose.runtime.LoggingStateChangeObserver
import com.share.external.lib.compose.state.StateProvider
import com.share.external.lib.mvvm.base.View
import com.share.external.lib.mvvm.base.ViewProvider
import com.share.sample.app.SampleApplication
import com.share.sample.app.theme.MainTheme
import com.share.sample.feature.onboarding.OnboardingComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module(subcomponents = [OnboardingComponent::class])
object MainViewModule {
    @MainViewScope
    @Provides
    fun navigationController(scope: MainViewProviderScope) = MainViewNavigationController(scope = scope)
}

interface MainViewDependency {
    val navigationController: MainViewNavigationController
    val onboarding: OnboardingComponent.Factory
}

class MainViewProvider(
    private val dependency: MainViewDependency,
) : ViewProvider {
    constructor(
        application: SampleApplication,
        parentScope: ManagedCoroutineScope
    ): this(
        dependency = application.sampleActivityViewModelComponent(parent = parentScope)
    )

    override fun onViewAppear(scope: CoroutineScope) =
        MainView(
            navigationController = dependency.navigationController,
            onboarding = dependency.onboarding,
            scope = scope
        )
}

class MainView(
    private val navigationController: MainViewNavigationController,
    private val onboarding: OnboardingComponent.Factory,
    override val scope: CoroutineScope,
) : View, StateProvider, LoggingStateChangeObserver {
    override val content: @Composable () -> Unit = {
        MainTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.padding(paddingValues = it)) {
                    navigationController.Content { route, scope ->
                        when (route) {
                            is ActivityViewRoute.LoggedIn ->
                                ViewProvider {
                                    View {
                                        Text(text = "Logged in as ${route.user}")
                                        Button(onClick = {
                                            navigationController.selected = ActivityViewRoute.LoggedOut
                                        }) {
                                            Text("Log Out")
                                        }
                                    }
                                }

                            ActivityViewRoute.LoggedOut -> {
                                onboarding(scope = OnboardingComponent.Scope(actual = scope))
                            }
                        }
                    }
                }
            }
        }
    }
}
