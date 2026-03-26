package com.share.sample.feature.details.creator.viewall

import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.Screen
import com.share.sample.core.data.api.ArtistResult
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.MediaType
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Scope for the View All creator screen.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ViewAllCreatorScope

/**
 * Dagger subcomponent for the View All creator screen.
 *
 * This component shows a full-screen list of all items by a creator,
 * allowing navigation to item details.
 */
@ViewAllCreatorScope
@Subcomponent(modules = [ViewAllCreatorModule::class, ViewAllCreatorViewModule::class])
interface ViewAllCreatorComponent {
    val viewProvider: ViewAllCreatorViewProvider

    /**
     * Dynamic dependencies including navigation scope and creator's items.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val creator: Creator,
        val items: List<ArtistResult>,
        val mediaType: MediaType,
    )

    @Subcomponent.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String get() = "ViewAllCreator"

        abstract fun create(@BindsInstance dependency: Dependency): ViewAllCreatorComponent

        override fun create(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }
}

@Module
object ViewAllCreatorModule
