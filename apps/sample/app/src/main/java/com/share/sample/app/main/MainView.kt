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
import com.share.external.lib.activity.Main
import com.share.external.lib.compose.runtime.LoggingStateChangeObserver
import com.share.external.lib.compose.state.StateProvider
import com.share.external.lib.core.View
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.switcher.ViewSwitcher
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

@Main(application = SampleApplication::class)
class MainViewProvider(
    private val dependency: MainViewDependency,
) : ViewProvider {
    constructor(
        application: SampleApplication,
        coroutineScope: CoroutineScope
    ): this(
        dependency = application.sampleActivityViewModelComponent(coroutineScope = coroutineScope)
    )

    override fun onViewAppear(scope: CoroutineScope) =
        MainView(
            navigationController = dependency.navigationController,
            onboarding = { dependency.onboarding(scope = OnboardingComponent.Scope(actual = it)) },
            scope = scope
        )
}

class MainView(
    private val navigationController: ViewSwitcher<ActivityViewRoute>,
    private val onboarding: (ManagedCoroutineScope) -> ViewProvider,
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

                            ActivityViewRoute.LoggedOut -> onboarding(scope)
                        }
                    }
                }
            }
        }
    }
}
