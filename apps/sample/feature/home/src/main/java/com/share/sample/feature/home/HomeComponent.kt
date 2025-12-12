package com.share.sample.feature.home

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewProvider
import com.share.sample.feature.details.DetailsComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Scope for the home feature.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class HomeScope

/**
 * Dagger subcomponent for the home feature.
 *
 * This component manages the home tab which displays the RSS feed browser
 * with category selection and item list.
 */
@HomeScope
@Subcomponent(modules = [HomeModule::class, HomeViewModule::class])
interface HomeComponent {
    val viewProvider: HomeViewProvider

    /**
     * Dynamic dependencies for the Home feature.
     */
    class Dependency(val scope: ManagedCoroutineScope)

    @Subcomponent.Factory
    abstract class Factory: (ManagedCoroutineScope) -> ViewProvider {
        abstract fun create(@BindsInstance dependency: Dependency): HomeComponent

        override operator fun invoke(scope: ManagedCoroutineScope): ViewProvider {
            return create(Dependency(scope)).viewProvider
        }
    }
}

@Module(subcomponents = [CategoryFeedComponent::class, DetailsComponent::class])
object HomeModule
