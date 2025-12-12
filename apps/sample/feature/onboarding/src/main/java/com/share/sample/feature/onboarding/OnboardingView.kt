package com.share.sample.feature.onboarding

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.navigation.stack.ModalNavigationStack
import com.share.external.lib.navigation.stack.NavigationRoute
import com.share.external.lib.navigation.stack.NavigationStackHost
import com.share.external.lib.navigation.stack.Screen
import com.share.external.lib.navigation.stack.toNavigationRoute
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewProvider
import com.share.sample.feature.onboarding.signin.SignInComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object OnboardingViewModule {
    @OnboardingScope
    @Provides
    fun onboardingViewProvider(
        dependency: OnboardingComponent.Dependency,
        signIn: SignInComponent.Factory
    ) = OnboardingViewProvider(
        scope = dependency.scope,
        signInRoute = signIn.toNavigationRoute()
    )
}

class OnboardingViewProvider(
    scope: ManagedCoroutineScope,
    signInRoute: NavigationRoute<Screen>,
) : ViewProvider {
    val navigationStack = ModalNavigationStack(
        rootScope = scope,
        initialStack = { it.push(signInRoute) }
    )

    override fun onViewAppear(scope: CoroutineScope) = View {
        NavigationStackHost(
            name = "OnboardingNavigationStackHost",
            backHandlerEnabled = navigationStack.size > 1,
            stack = navigationStack,
        ) {}
    }
}
