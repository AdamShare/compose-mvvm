package com.share.sample.feature.details

import com.share.external.lib.navigation.stack.NavigationStackEntry
import com.share.external.lib.navigation.stack.NavigationRouteFactory
import com.share.external.lib.navigation.stack.Screen
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType
import com.share.sample.feature.details.creator.CreatorModalComponent
import com.share.sample.feature.details.genre.GenreComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Qualifier
import javax.inject.Scope

/**
 * Scope for the details feature.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class DetailsScope

/**
 * Qualifier for Details-specific bindings.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Details

/**
 * Dagger subcomponent for the details feature.
 *
 * This component manages the item details screen, which can be
 * reused from both the Home and Favorites tabs.
 */
@DetailsScope
@Subcomponent(modules = [DetailsModule::class, DetailsViewModelModule::class, DetailsViewModule::class])
interface DetailsComponent {
    val viewProvider: DetailsViewProvider

    /**
     * Dynamic dependencies for this details instance.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val feedItem: FeedItem,
        val mediaType: MediaType
    )

    @Subcomponent.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String get() = "Details"

        abstract fun create(@BindsInstance dependency: Dependency): DetailsComponent

        override fun invoke(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }
}

@Module(subcomponents = [CreatorModalComponent::class, GenreComponent::class])
object DetailsModule
