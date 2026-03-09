package com.share.sample.feature.details.genre

import com.getbackcompose.navigation.stack.NavigationRouteFactory
import com.getbackcompose.navigation.stack.NavigationStackEntry
import com.getbackcompose.navigation.stack.Screen
import com.share.sample.core.data.model.Genre
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Scope marker for genre screen.
 * Defines the lifetime boundary for genre-scoped dependencies.
 */
object GenreScope

/**
 * Metro graph extension for the genre screen.
 *
 * Replaces Dagger's @Subcomponent. Uses explicit @Provides for all dependencies.
 * Scoped with @SingleIn(GenreScope::class) for per-instance caching.
 * Manages the genre details screen, which is pushed onto the navigation stack from the details screen.
 */
@SingleIn(GenreScope::class)
@GraphExtension(scope = GenreScope::class)
interface GenreGraph {
    val viewProvider: GenreViewProvider

    /**
     * Dynamic dependencies including navigation scope and genre info.
     */
    class Dependency(
        val navigationScope: NavigationStackEntry<Screen>,
        val genre: Genre
    )

    /**
     * Factory for creating GenreGraph instances.
     * Implements NavigationRouteFactory for navigation.
     */
    @GraphExtension.Factory
    abstract class Factory : NavigationRouteFactory<Dependency, Screen> {
        override val name: String get() = "Genre"

        abstract fun create(@Provides dependency: Dependency): GenreGraph

        override fun invoke(dependency: Dependency): Screen {
            return create(dependency).viewProvider
        }
    }

    /**
     * Provider functions for genre-specific dependencies.
     * All explicitly wired - no hidden @Inject magic.
     */
    companion object {
        @SingleIn(GenreScope::class)
        @Provides
        fun viewProvider(
            dependency: Dependency
        ): GenreViewProvider = GenreViewProvider(
            navigationStack = dependency.navigationScope,
            genre = dependency.genre
        )
    }
}
