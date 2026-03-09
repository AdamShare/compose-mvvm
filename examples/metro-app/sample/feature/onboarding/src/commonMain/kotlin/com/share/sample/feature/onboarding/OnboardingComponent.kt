package com.share.sample.feature.onboarding

import com.getbackcompose.core.ViewProvider
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.stack.toNavigationRoute
import com.share.sample.feature.onboarding.signin.SignInGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for onboarding feature.
 * Defines the lifetime boundary for onboarding-scoped dependencies.
 */
object OnboardingScope

/**
 * Metro graph extension for the onboarding feature.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(OnboardingScope::class) for per-instance caching.
 */
@SingleIn(OnboardingScope::class)
@GraphExtension(scope = OnboardingScope::class)
interface OnboardingGraph {
    val viewProvider: OnboardingViewProvider
    val signInGraphFactory: SignInGraph.Factory

    /**
     * Dynamic dependencies for the Onboarding feature.
     */
    class Dependency(val scope: ManagedCoroutineScope)

    /**
     * Factory for creating OnboardingGraph instances.
     * Function type for creating ViewProvider from scope.
     */
    @GraphExtension.Factory
    abstract class Factory : (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@Provides dependency: Dependency): OnboardingGraph

        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Dependency(scope)).viewProvider
        }
    }

    /**
     * Provider functions for onboarding-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(OnboardingScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency,
            signIn: SignInGraph.Factory,
        ): OnboardingViewProvider = OnboardingViewProvider(
            scope = dependency.scope,
            signInRoute = signIn.toNavigationRoute(),
        )
    }
}
