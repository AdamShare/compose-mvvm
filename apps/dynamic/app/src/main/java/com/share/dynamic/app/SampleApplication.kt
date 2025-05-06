package com.share.dynamic.app

import android.app.Application
import com.share.external.lib.mvvm.application.ApplicationCoroutineScope
import com.share.external.lib.mvvm.application.inject
import com.share.dynamic.app.activity.SampleActivityViewModelComponent
import timber.log.Timber
import javax.inject.Inject

class SampleApplication: Application(), SampleActivityViewModelComponent.Application {
    @Inject
    lateinit var applicationCoroutineScope: ApplicationCoroutineScope
    @Inject
    override lateinit var sampleActivityViewModelComponent: SampleActivityViewModelComponent.Factory

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        DaggerSampleApplicationComponent.factory().inject(this)
    }
}