package com.share.sample.feature.profile

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewProvider
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Scope for the profile feature.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ProfileScope

/**
 * Dagger subcomponent for the profile feature.
 *
 * This component manages the profile screen where users can view
 * their account info and log out.
 */
@ProfileScope
@Subcomponent(modules = [ProfileModule::class, ProfileViewModelModule::class, ProfileViewModule::class])
interface ProfileComponent {
    val viewProvider: ProfileViewProvider

    /**
     * Scope wrapper for the Profile coroutine scope.
     */
    class Scope(actual: ManagedCoroutineScope) : ManagedCoroutineScope by actual

    @Subcomponent.Factory
    abstract class Factory: (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@BindsInstance scope: Scope): ProfileComponent

        /**
         * Creates a ViewProvider for the profile feature.
         */
        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Scope(scope)).viewProvider
        }
    }
}

@Module
object ProfileModule
