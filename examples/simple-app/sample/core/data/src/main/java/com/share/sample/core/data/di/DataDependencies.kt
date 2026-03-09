package com.share.sample.core.data.di

import android.app.Application
import com.share.sample.core.data.api.AppleRssClient
import com.share.sample.core.data.repository.AndroidFavoritesStorage
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FavoritesStorage
import com.share.sample.core.data.repository.FeedRepository

/**
 * Manual dependency provider for data module (simple-app).
 * Creates instances of data-related dependencies.
 */
object DataDependencies {

    fun provideAppleRssClient(): AppleRssClient {
        return AppleRssClient()
    }

    fun provideFeedRepository(): FeedRepository {
        return FeedRepository(provideAppleRssClient())
    }

    fun provideFavoritesStorage(application: Application): FavoritesStorage {
        return AndroidFavoritesStorage(application)
    }

    fun provideFavoritesRepository(application: Application): FavoritesRepository {
        return FavoritesRepository(provideFavoritesStorage(application))
    }
}
