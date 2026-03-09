package com.share.sample.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.getbackcompose.core.VisibilityScopedView
import com.share.sample.core.auth.InMemoryCredentialsStorage
import com.share.sample.core.data.repository.InMemoryFavoritesStorage
import com.share.sample.integrations.main.SampleApplicationGraph
import dev.zacsweers.metro.createGraphFactory
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val graph = createGraphFactory<SampleApplicationGraph.Factory>().create(
        credentialsStorage = InMemoryCredentialsStorage(),
        favoritesStorage = InMemoryFavoritesStorage(),
    )

    ComposeViewport(document.body!!) {
        val view = VisibilityScopedView(
            scopeFactory = { CoroutineScope(SupervisorJob() + Dispatchers.Main) },
            onViewAppear = { scope ->
                graph.mainViewGraphFactory(scope).viewProvider.onViewAppear(scope)
            }
        )
        view.content()
    }
}
