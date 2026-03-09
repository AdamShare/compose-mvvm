package com.share.sample.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.getbackcompose.core.VisibilityScopedView
import com.share.sample.core.auth.InMemoryCredentialsStorage
import com.share.sample.core.data.repository.InMemoryFavoritesStorage
import com.share.sample.integrations.main.SampleApplicationGraph
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun main() = application {
    val graph = createGraphFactory<SampleApplicationGraph.Factory>().create(
        credentialsStorage = InMemoryCredentialsStorage(),
        favoritesStorage = InMemoryFavoritesStorage(),
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "GetBack Metro Sample"
    ) {
        val view = VisibilityScopedView(
            scopeFactory = { CoroutineScope(SupervisorJob() + Dispatchers.Main) },
            onViewAppear = { scope ->
                graph.mainViewGraphFactory(scope).viewProvider.onViewAppear(scope)
            }
        )
        view.content()
    }
}
