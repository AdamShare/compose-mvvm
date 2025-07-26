package com.share.sample.app.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.share.external.lib.activity.ViewModelComponentActivity
import com.share.sample.app.SampleApplication
import com.share.sample.app.main.MainViewProvider
import kotlinx.coroutines.CoroutineScope

open class MainActivity : ViewModelComponentActivity<SampleApplication, MainViewProvider>() {
    // To generate
    final override fun buildProvider(
        application: SampleApplication,
        coroutineScope: CoroutineScope
    ) = MainViewProvider(
        application = application,
        coroutineScope = coroutineScope
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}
