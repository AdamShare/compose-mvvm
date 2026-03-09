package com.share.sample.feature.profile

import com.getbackcompose.core.ViewProvider
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.share.sample.core.auth.AuthRepository
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for profile feature.
 * Defines the lifetime boundary for profile-scoped dependencies.
 */
object ProfileScope

/**
 * Metro graph extension for the profile feature.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(ProfileScope::class) for per-instance caching.
 * Manages the profile screen where users can view their account info and log out.
 */
@SingleIn(ProfileScope::class)
@GraphExtension(scope = ProfileScope::class)
interface ProfileGraph {
    val viewProvider: ProfileViewProvider

    /**
     * Scope wrapper for the Profile coroutine scope.
     */
    class Scope(actual: ManagedCoroutineScope) : ManagedCoroutineScope by actual

    /**
     * Factory for creating ProfileGraph instances.
     * Function type for creating ViewProvider from scope.
     */
    @GraphExtension.Factory
    abstract class Factory : (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@Provides scope: Scope): ProfileGraph

        /**
         * Creates a ViewProvider for the profile feature.
         */
        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Scope(scope)).viewProvider
        }
    }

    /**
     * Provider functions for profile-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(ProfileScope::class)
        @Provides
        fun viewModel(
            scope: Scope,
            authRepository: AuthRepository,
        ): ProfileViewModel = ProfileViewModel(
            scopeFactory = scope,
            authRepository = authRepository,
        )

        @SingleIn(ProfileScope::class)
        @Provides
        fun viewProvider(
            viewModel: ProfileViewModel,
        ): ProfileViewProvider = ProfileViewProvider(
            viewModel = viewModel,
        )
    }
}
