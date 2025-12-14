package com.share.sample.core.data

import android.app.Application
import com.share.sample.core.data.api.AppleRssClient
import com.share.sample.core.data.repository.FavoritesRepository
import com.share.sample.core.data.repository.FeedRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataModule {
    @Singleton
    @Provides
    fun appleRssClient() = AppleRssClient()

    @Singleton
    @Provides
    fun feedRepository(client: AppleRssClient) = FeedRepository(client)

    @Singleton
    @Provides
    fun favoritesRepository(application: Application) = FavoritesRepository(application)
}
