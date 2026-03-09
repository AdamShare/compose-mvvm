package com.share.sample.app

import android.app.Application
import com.getbackcompose.activity.application.ApplicationCoroutineScopeFactory
import com.share.sample.core.auth.AndroidCredentialsStorage
import com.share.sample.core.data.repository.AndroidFavoritesStorage
import com.share.sample.integrations.main.SampleApplicationGraph
import dev.zacsweers.metro.createGraphFactory

class SampleApplication : Application(), ApplicationCoroutineScopeFactory {
    val graph by lazy {
        createGraphFactory<SampleApplicationGraph.Factory>().create(
            credentialsStorage = AndroidCredentialsStorage(this),
            favoritesStorage = AndroidFavoritesStorage(this),
        )
    }
}
