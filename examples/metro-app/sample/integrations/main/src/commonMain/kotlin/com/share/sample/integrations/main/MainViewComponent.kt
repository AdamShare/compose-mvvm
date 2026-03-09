package com.share.sample.integrations.main

import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.share.sample.core.auth.AuthRepository
import com.share.sample.feature.main.MainTabGraph
import com.share.sample.feature.onboarding.OnboardingGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope

/**
 * Scope marker for main view.
 * Defines the lifetime boundary for main view-scoped dependencies.
 */
object MainViewScope

/**
 * Metro graph extension for the main view.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(MainViewScope::class) for per-instance caching.
 * Manages the root view that switches between onboarding and main tab navigation.
 */
@SingleIn(MainViewScope::class)
@GraphExtension(scope = MainViewScope::class)
interface MainViewGraph {
    val viewProvider: MainViewProvider
    val mainTabGraphFactory: MainTabGraph.Factory
    val onboardingGraphFactory: OnboardingGraph.Factory

    /**
     * Factory for creating MainViewGraph instances.
     */
    @GraphExtension.Factory
    abstract class Factory {
        abstract fun create(@Provides scope: MainViewProviderScope): MainViewGraph

        operator fun invoke(coroutineScope: CoroutineScope): MainViewGraph {
            return create(scope = MainViewProviderScope(actual = coroutineScope))
        }
    }

    /**
     * Provider functions for main view-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(MainViewScope::class)
        @Provides
        fun mainViewSwitcher(scope: MainViewProviderScope): MainViewSwitcher =
            MainViewSwitcher(scope)

        @SingleIn(MainViewScope::class)
        @Provides
        fun viewProvider(
            scope: MainViewProviderScope,
            authRepository: AuthRepository,
            mainTabGraphFactory: MainTabGraph.Factory,
            onboardingGraphFactory: OnboardingGraph.Factory,
            viewSwitcher: MainViewSwitcher,
        ): MainViewProvider = MainViewProvider(
            authRepository = authRepository,
            mainTabFactory = mainTabGraphFactory,
            onboardingFactory = onboardingGraphFactory,
            scope = scope.create("MainView"),
            viewSwitcher = viewSwitcher,
        )
    }
}

class MainViewProviderScope(actual: CoroutineScope) : ManagedCoroutineScope by ManagedCoroutineScope(actual = actual)
