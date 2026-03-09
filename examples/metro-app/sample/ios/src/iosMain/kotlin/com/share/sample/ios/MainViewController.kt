package com.share.sample.ios

import androidx.compose.ui.window.ComposeUIViewController
import com.getbackcompose.core.VisibilityScopedView
import com.share.sample.core.auth.InMemoryCredentialsStorage
import com.share.sample.core.data.repository.InMemoryFavoritesStorage
import com.share.sample.integrations.main.SampleApplicationGraph
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val graph = createGraphFactory<SampleApplicationGraph.Factory>().create(
        credentialsStorage = InMemoryCredentialsStorage(),
        favoritesStorage = InMemoryFavoritesStorage(),
    )

    return ComposeUIViewController {
        val view = VisibilityScopedView(
            scopeFactory = { CoroutineScope(SupervisorJob() + Dispatchers.Main) },
            onViewAppear = { scope ->
                graph.mainViewGraphFactory(scope).viewProvider.onViewAppear(scope)
            }
        )
        view.content()
    }
}
