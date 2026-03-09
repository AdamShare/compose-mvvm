package com.share.sample.app.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.getbackcompose.activity.ViewModelComponentActivity
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.share.sample.app.MainViewProvider
import com.share.sample.app.SampleApplication
import kotlinx.coroutines.CoroutineScope

open class MainActivity : ViewModelComponentActivity<SampleApplication, MainViewProvider>() {
    final override fun buildProvider(
        application: SampleApplication,
        coroutineScope: CoroutineScope
    ) = MainViewProvider(
        authRepository = application.authRepository,
        feedRepository = application.feedRepository,
        favoritesRepository = application.favoritesRepository,
        managedScope = ManagedCoroutineScope(actual = coroutineScope)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}
