package com.share.sample.app

import android.app.Application
import com.share.external.lib.activity.application.ApplicationCoroutineScopeFactory
import com.share.sample.integrations.main.DaggerSampleApplicationComponent
import com.share.sample.integrations.main.SampleApplicationComponent

class SampleApplication : Application(), ApplicationCoroutineScopeFactory {
    lateinit var component: SampleApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()
        component = DaggerSampleApplicationComponent.factory().invoke(this, this)
    }
}
