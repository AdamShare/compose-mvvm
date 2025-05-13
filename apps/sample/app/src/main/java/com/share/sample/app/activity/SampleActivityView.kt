package com.share.sample.app.activity

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.share.external.lib.mvvm.navigation.content.View
import com.share.sample.feature.onboarding.OnboardingComponent

class SampleActivityView(
    private val navigationController: ActivityViewNavigationController,
    private val onboarding: OnboardingComponent.Factory
): View {
    override val content = @Composable {
            navigationController.Content { route, scope ->
                when (route) {
                    is ActivityViewRoute.LoggedIn -> {
                         {
                            Text("Logged in as ${route.user}")

                            Button(onClick = {
                                navigationController.selected = ActivityViewRoute.LoggedOut
                            }) {
                                Text("Log Out")
                            }
                        }
                    }

                    ActivityViewRoute.LoggedOut -> {
                        onboarding(
                            scope = OnboardingComponent.Scope(scope)
                        ).content
                    }
                }
            }
    }
}