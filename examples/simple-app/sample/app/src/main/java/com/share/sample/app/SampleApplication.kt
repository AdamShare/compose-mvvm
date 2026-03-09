package com.share.sample.app

import android.app.Application
import com.getbackcompose.activity.application.ApplicationCoroutineScopeFactory
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.auth.di.AuthDependencies
import com.share.sample.core.data.di.DataDependencies
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository

/**
 * Simple application class with manual dependency wiring.
 * No DI framework - dependencies are created and stored as singletons here.
 */
class SampleApplication : Application(), ApplicationCoroutineScopeFactory {

    // Core singleton dependencies
    val authRepository: AuthRepository by lazy {
        AuthDependencies.provideAuthRepository(this)
    }

    val feedRepository: FeedRepository by lazy {
        DataDependencies.provideFeedRepository()
    }

    val favoritesRepository: FavoritesRepository by lazy {
        DataDependencies.provideFavoritesRepository(this)
    }
}
