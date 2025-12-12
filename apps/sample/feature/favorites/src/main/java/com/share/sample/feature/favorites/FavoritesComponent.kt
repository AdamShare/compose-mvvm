package com.share.sample.feature.favorites

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewProvider
import com.share.sample.feature.details.DetailsComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Scope for the favorites feature.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class FavoritesScope

/**
 * Dagger subcomponent for the favorites feature.
 *
 * This component manages the favorites tab which displays the user's
 * saved favorite items.
 */
@FavoritesScope
@Subcomponent(modules = [FavoritesModule::class, FavoritesViewModelModule::class, FavoritesViewModule::class])
interface FavoritesComponent {
    val viewProvider: FavoritesViewProvider

    /**
     * Dynamic dependencies for the Favorites feature.
     */
    class Dependency(val scope: ManagedCoroutineScope)

    @Subcomponent.Factory
    abstract class Factory: (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@BindsInstance dependency: Dependency): FavoritesComponent

        /**
         * Creates a ViewProvider for the favorites feature.
         */
        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Dependency(scope)).viewProvider
        }
    }
}

@Module(subcomponents = [DetailsComponent::class])
object FavoritesModule
