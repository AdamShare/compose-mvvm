package com.share.sample.app

import android.app.Application
import com.share.external.lib.activity.application.ApplicationCoroutineScopeProvider
import com.share.sample.app.main.MainViewComponent
import javax.inject.Inject

class SampleApplication : Application(), ApplicationCoroutineScopeProvider {
    @Inject lateinit var sampleActivityViewModelComponent: MainViewComponent.Factory

    override fun onCreate() {
        super.onCreate()

        DaggerSampleApplicationComponent.factory().invoke(this, this).inject(this)
    }
}
