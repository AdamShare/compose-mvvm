package com.share.sample.feature.main

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewProvider
import com.share.sample.feature.favorites.FavoritesComponent
import com.share.sample.feature.home.HomeComponent
import com.share.sample.feature.profile.ProfileComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Scope for the main tab container.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class MainTabScope

/**
 * Dagger subcomponent for the main tab container.
 *
 * This component manages the tab-based navigation when the user is logged in.
 */
@MainTabScope
@Subcomponent(modules = [MainTabModule::class, MainTabViewModule::class])
interface MainTabComponent {
    val viewProvider: MainTabViewProvider

    /**
     * Scope wrapper for the MainTab coroutine scope.
     */
    class Scope(actual: ManagedCoroutineScope) : ManagedCoroutineScope by actual

    @Subcomponent.Factory
    abstract class Factory: (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@BindsInstance scope: Scope): MainTabComponent

        /**
         * Creates a ViewProvider for the main tab container.
         */
        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Scope(scope)).viewProvider
        }
    }
}

/**
 * Module that declares subcomponents for each tab.
 * These will be added as the feature modules are created.
 */
@Module(subcomponents = [
    HomeComponent::class,
    FavoritesComponent::class,
    ProfileComponent::class
])
object MainTabModule
