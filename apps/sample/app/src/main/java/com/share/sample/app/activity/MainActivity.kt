package com.share.sample.app.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.share.external.lib.activity.ViewModelComponentActivity
import com.share.sample.app.SampleApplication
import com.share.sample.integrations.main.MainViewComponent
import com.share.sample.integrations.main.MainViewProvider
import kotlinx.coroutines.CoroutineScope

open class MainActivity : ViewModelComponentActivity<SampleApplication, MainViewProvider>() {
    final override fun buildProvider(
        application: SampleApplication,
        coroutineScope: CoroutineScope
    ) = application.component.mainViewComponentFactory(coroutineScope).viewProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}
