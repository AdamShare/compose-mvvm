package com.share.sample.feature.onboarding.signin

import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.Screen
import com.getbackcompose.navigation.stack.toNavigationRoute
import com.share.sample.core.auth.AuthRepository
import com.share.sample.feature.onboarding.signin.signup.SignUpGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for sign in screen.
 * Defines the lifetime boundary for sign in-scoped dependencies.
 */
object SignInScope

/**
 * Metro graph extension for the sign in screen.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(SignInScope::class) for per-instance caching.
 */
@SingleIn(SignInScope::class)
@GraphExtension(scope = SignInScope::class)
interface SignInGraph {
    val viewProvider: SignInViewProvider
    val signUpGraphFactory: SignUpGraph.Factory

    /**
     * Dynamic dependencies for sign in screen.
     */
    class Dependency(val navigationStackEntry: NavigationStackEntry<Screen>)

    /**
     * Factory for creating SignInGraph instances.
     * Implements NavigationRouteFactory for navigation.
     */
    @GraphExtension.Factory
    abstract class Factory : NavigationRouteFactory<NavigationStackEntry<Screen>, Screen> {
        override val name: String get() = "SignIn"

        abstract fun create(@Provides dependency: Dependency): SignInGraph

        override fun invoke(scope: NavigationStackEntry<Screen>): Screen {
            return create(Dependency(scope)).viewProvider
        }
    }

    /**
     * Provider functions for sign in-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(SignInScope::class)
        @Provides
        fun emailViewModel(
            dependency: Dependency
        ): EmailViewModel = EmailViewModel(
            scope = dependency.navigationStackEntry
        )

        @SingleIn(SignInScope::class)
        @Provides
        fun signInViewModel(
            emailViewModel: EmailViewModel,
            authRepository: AuthRepository
        ): SignInViewModel = SignInViewModel(
            emailViewModel = emailViewModel,
            authRepository = authRepository
        )

        @SingleIn(SignInScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            emailViewModel: EmailViewModel,
            signUpGraphFactory: SignUpGraph.Factory,
            signInViewModel: SignInViewModel,
        ): SignInViewProvider = SignInViewProvider(
            emailViewModel = emailViewModel,
            navigationStack = dependency.navigationStackEntry,
            signUpRoute = signUpGraphFactory.toNavigationRoute(),
            signInViewModel = signInViewModel,
        )
    }
}
