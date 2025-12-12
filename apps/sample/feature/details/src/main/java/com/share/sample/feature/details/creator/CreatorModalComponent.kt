package com.share.sample.feature.details.creator

import com.share.external.lib.navigation.stack.NavigationStackEntry
import com.share.external.lib.navigation.stack.NavigationRouteFactory
import com.share.external.lib.navigation.stack.Screen
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType
import com.share.sample.feature.details.creator.viewall.ViewAllCreatorComponent
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Scope for the creator modal.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class CreatorModalScope


/**
 * Dagger subcomponent for the creator modal.
 *
 * This component manages the modal presentation that shows
 * other works by the same creator/artist.
 */
@CreatorModalScope
@Subcomponent(modules = [CreatorModalModule::class, CreatorModalViewModelModule::class, CreatorModalViewModule::class])
interface CreatorModalComponent {
    val viewProvider: CreatorModalViewProvider

    /**
     * Dynamic dependencies for this creator modal instance.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val creator: Creator,
        val mediaType: MediaType,
    )

    @Subcomponent.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String get() = "CreatorModal"

        abstract fun create(@BindsInstance dependency: Dependency): CreatorModalComponent

        override fun invoke(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }
}

@Module(subcomponents = [ViewAllCreatorComponent::class])
object CreatorModalModule
