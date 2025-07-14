package com.share.sample.app

import android.app.Application
import com.share.external.lib.activity.application.ApplicationCoroutineScope
import com.share.external.lib.activity.application.inject
import com.share.sample.app.activity.SampleActivityViewModelComponent
import javax.inject.Inject
import timber.log.Timber

class SampleApplication : Application(), SampleActivityViewModelComponent.Application {
    @Inject lateinit var applicationCoroutineScope: ApplicationCoroutineScope
    @Inject override lateinit var sampleActivityViewModelComponent: SampleActivityViewModelComponent.Factory

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        DaggerSampleApplicationComponent.factory().inject(this)
    }
}
