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
import com.share.external.lib.mvvm.base.View
import com.share.external.lib.mvvm.base.ViewProvider
import com.share.sample.app.SampleApplication
import com.share.sample.feature.onboarding.OnboardingComponent
import kotlinx.coroutines.CoroutineScope

class MainViewProvider(application: SampleApplication, parentScope: ManagedCoroutineScope) : ViewProvider {
    private val component = application.sampleActivityViewModelComponent(parentScope)

    override fun create(scope: CoroutineScope) = MainView(
            navigationController = component.navigationController,
            onboarding = component.onboarding
        )
}

class MainView(
    private val navigationController: MainViewNavigationController,
    private val onboarding: OnboardingComponent.Factory,
) : View {
    override val content: @Composable () -> Unit = {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.padding(paddingValues = it)) {
                navigationController.Content { route, scope ->
                    when (route) {
                        is ActivityViewRoute.LoggedIn ->
                            ViewProvider {
                                View {
                                    Text(text = "Logged in as ${route.user}")
                                    Button(onClick = { navigationController.selected = ActivityViewRoute.LoggedOut }) {
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
