package com.share.sample.feature.onboarding

import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.stack.ModalNavigationStack
import com.getbackcompose.navigation.stack.NavigationRoute
import com.getbackcompose.navigation.stack.NavigationStackHost
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewProvider
import com.share.sample.core.auth.AuthRepository
import com.share.sample.feature.onboarding.signin.SignInViewProvider
import kotlinx.coroutines.CoroutineScope

/**
 * Routes for the onboarding navigation.
 */
enum class OnboardingRoute : com.getbackcompose.core.ViewKey {
    SignIn
}

class OnboardingViewProvider(
    private val scope: ManagedCoroutineScope,
    private val authRepository: AuthRepository,
) : ViewProvider {
    val navigationStack = ModalNavigationStack<Screen>(
        rootScope = scope,
        initialStack = { navStack ->
            navStack.push(
                NavigationRoute(
                    key = OnboardingRoute.SignIn,
                    factory = { navScope ->
                        SignInViewProvider(
                            authRepository = authRepository,
                            navigationStack = navScope,
                            managedScope = scope
                        )
                    }
                )
            )
        }
    )

    override fun onViewAppear(scope: CoroutineScope) = View {
        NavigationStackHost<Screen>(
            name = "OnboardingNavigationStackHost",
            backHandlerEnabled = navigationStack.size > 1,
            stack = navigationStack,
        ) {}
    }
}
