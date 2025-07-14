package com.share.sample.app.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.share.external.lib.activity.ActivityViewModelCoroutineScope
import com.share.external.lib.activity.ViewModelComponentActivity
import com.share.sample.app.SampleApplication
import com.share.sample.app.main.MainViewProvider

// To generate
open class MainActivity : ViewModelComponentActivity<SampleApplication, MainViewProvider>() {
    final override fun buildProvider(
        application: SampleApplication,
        scope: ActivityViewModelCoroutineScope
    ): MainViewProvider {
        return MainViewProvider(
            application = application,
            parentScope = scope
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}
