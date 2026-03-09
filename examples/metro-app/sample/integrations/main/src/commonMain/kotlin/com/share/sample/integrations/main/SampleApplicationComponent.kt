package com.share.sample.integrations.main

import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.CredentialsStorage
import com.share.sample.core.data.api.AppleRssClient
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FavoritesStorage
import com.share.sample.core.data.repository.FeedRepository
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Metro dependency graph for the application.
 *
 * Platform-agnostic root graph. Platform-specific dependencies (storage implementations)
 * are passed in via the Factory, allowing Android and Desktop to provide their own implementations.
 */
@SingleIn(AppScope::class)
@DependencyGraph(scope = AppScope::class)
abstract class SampleApplicationGraph {
    abstract val mainViewGraphFactory: MainViewGraph.Factory

    @DependencyGraph.Factory
    abstract class Factory {
        abstract fun create(
            @Provides credentialsStorage: CredentialsStorage,
            @Provides favoritesStorage: FavoritesStorage,
        ): SampleApplicationGraph
    }

    companion object {
        @SingleIn(AppScope::class)
        @Provides
        fun authRepository(credentialsStorage: CredentialsStorage): AuthRepository =
            AuthRepository(credentialsStorage)

        @SingleIn(AppScope::class)
        @Provides
        fun appleRssClient(): AppleRssClient = AppleRssClient()

        @SingleIn(AppScope::class)
        @Provides
        fun feedRepository(client: AppleRssClient): FeedRepository =
            FeedRepository(client)

        @SingleIn(AppScope::class)
        @Provides
        fun favoritesRepository(storage: FavoritesStorage): FavoritesRepository =
            FavoritesRepository(storage)
    }
}
