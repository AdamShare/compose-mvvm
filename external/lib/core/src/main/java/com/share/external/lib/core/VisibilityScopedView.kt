package com.share.external.lib.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.share.external.lib.core.context.viewVisibilityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

@Stable
class VisibilityScopedView(
    private val scopeFactory: () -> CoroutineScope,
    private val onViewAppear: (CoroutineScope) -> View,
): View {
    constructor(
        scopeFactory: () -> CoroutineScope,
        viewProvider: ViewProvider,
        ): this(
        scopeFactory = scopeFactory,
        onViewAppear = viewProvider::onViewAppear
        )

    private var currentScope: CoroutineScope? = null
    private val currentView = mutableStateOf<View?>(null)

    override val content: @Composable () -> Unit = {
        viewVisibilityObserver(
            onVisible = {
                if (currentScope != null) {
                    // View context change can cause multiple calls before hidden.
                    currentView
                } else {
                    val scope = scopeFactory()
                    currentScope = scope
                    currentView.value = onViewAppear(scope)
                    currentView
                }
            },
            onHidden = {
                currentScope?.cancel(message = "View hidden")
                currentScope = null
            }
        ).value?.content?.invoke()
    }
}