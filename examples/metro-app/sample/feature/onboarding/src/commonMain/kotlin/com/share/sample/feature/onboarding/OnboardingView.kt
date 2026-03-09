package com.share.sample.feature.onboarding

import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.stack.ModalNavigationStack
import com.getbackcompose.navigation.stack.NavigationRoute
import com.getbackcompose.navigation.stack.NavigationStackHost
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.core.View
import com.getbackcompose.core.ViewProvider
import kotlinx.coroutines.CoroutineScope

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
