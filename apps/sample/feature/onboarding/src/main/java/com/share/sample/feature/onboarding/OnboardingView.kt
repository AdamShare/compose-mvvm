package com.share.sample.feature.onboarding

import com.share.external.lib.mvvm.navigation.content.Screen
import com.share.external.lib.mvvm.base.View
import com.share.external.lib.mvvm.base.ViewProvider
import com.share.external.lib.mvvm.navigation.stack.NavigationStackHost
import com.share.external.lib.mvvm.navigation.stack.ViewModelNavigationStack
import com.share.sample.feature.onboarding.signin.SignInComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object OnboardingViewModule {
    @OnboardingScope
    @Provides
    fun onboardingViewProvider(scope: OnboardingComponent.Scope, signIn: SignInComponent.Factory) =
        OnboardingViewProvider(navigationStack = ViewModelNavigationStack(scope, initialStack = { it.push(signIn) }))
}

class OnboardingViewProvider(private val navigationStack: ViewModelNavigationStack<Screen>) : ViewProvider {
    override fun onViewAppear(scope: CoroutineScope) = View {
        NavigationStackHost(
            analyticsId = "OnboardingNavigationStackHost",
            backHandlerEnabled = navigationStack.size > 1,
            navigationStack = navigationStack,
        ) {}
    }
}
