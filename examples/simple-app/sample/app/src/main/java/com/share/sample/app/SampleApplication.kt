package com.share.sample.app

import android.app.Application
import com.getbackcompose.activity.application.ApplicationCoroutineScopeFactory
import com.share.sample.core.auth.AuthRepository
import com.share.sample.core.data.AppleFeedClient

/**
 * Simple application class with manual dependency wiring.
 * No DI framework - dependencies are created and stored as singletons here.
 */
class SampleApplication : Application(), ApplicationCoroutineScopeFactory {

    // Singleton dependencies
    lateinit var authRepository: AuthRepository
        private set

    lateinit var feedClient: AppleFeedClient
        private set

    override fun onCreate() {
        super.onCreate()

        // Manually create singleton dependencies
        authRepository = AuthRepository()
        feedClient = AppleFeedClient()
    }
}
