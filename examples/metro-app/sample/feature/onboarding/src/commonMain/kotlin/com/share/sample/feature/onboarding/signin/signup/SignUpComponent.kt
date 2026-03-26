package com.share.sample.feature.onboarding.signin.signup

import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.Screen
import com.share.sample.core.auth.AuthRepository
import com.share.sample.feature.onboarding.signin.EmailViewModel
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for sign up screen.
 * Defines the lifetime boundary for sign up-scoped dependencies.
 */
object SignUpScope

/**
 * Metro graph extension for the sign up screen.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(SignUpScope::class) for per-instance caching.
 */
@SingleIn(SignUpScope::class)
@GraphExtension(scope = SignUpScope::class)
interface SignUpGraph {
    val screen: SignUpScreen

    /**
     * Dynamic dependencies for sign up screen.
     */
    class Dependency(val navigationStackEntry: NavigationStackEntry<Screen>)

    /**
     * Factory for creating SignUpGraph instances.
     * Implements NavigationRouteFactory for navigation.
     */
    @GraphExtension.Factory
    abstract class Factory : NavigationRouteFactory<NavigationStackEntry<Screen>, Screen> {
        override val name: String get() = "SignUp"

        abstract fun create(@Provides dependency: Dependency): SignUpGraph

        override fun create(navigationStackEntry: NavigationStackEntry<Screen>): Screen {
            return create(Dependency(navigationStackEntry)).screen
        }
    }

    /**
     * Provider functions for sign up-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(SignUpScope::class)
        @Provides
        fun emailViewModel(
            dependency: Dependency
        ): EmailViewModel = EmailViewModel(
            scope = dependency.navigationStackEntry
        )

        @SingleIn(SignUpScope::class)
        @Provides
        fun viewModel(
            authRepository: AuthRepository,
            dependency: Dependency,
        ): SignUpViewModel = SignUpViewModel(
            authRepository = authRepository,
            scope = dependency.navigationStackEntry,
        )

        @SingleIn(SignUpScope::class)
        @Provides
        fun screen(
            dependency: Dependency,
            viewModel: SignUpViewModel,
        ): SignUpScreen = SignUpScreen(
            viewModel = viewModel,
        )
    }
}
