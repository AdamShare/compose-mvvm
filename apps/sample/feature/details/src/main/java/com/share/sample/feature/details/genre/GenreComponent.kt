package com.share.sample.feature.details.genre

import com.share.external.lib.navigation.stack.NavigationStackEntry
import com.share.external.lib.navigation.stack.NavigationRouteFactory
import com.share.external.lib.navigation.stack.Screen
import com.share.sample.core.data.model.Genre
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

/**
 * Scope for the genre screen.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class GenreScope

/**
 * Dagger subcomponent for the genre screen.
 *
 * This component manages the genre details screen, which is pushed
 * onto the navigation stack from the details screen.
 */
@GenreScope
@Subcomponent(modules = [GenreModule::class, GenreViewModule::class])
interface GenreComponent {
    val viewProvider: GenreViewProvider

    /**
     * Dynamic dependencies including navigation scope and genre info.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val genre: Genre
    )

    @Subcomponent.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String get() = "Genre"

        abstract fun create(@BindsInstance dependency: Dependency): GenreComponent

        override fun invoke(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }
}

@Module
object GenreModule
