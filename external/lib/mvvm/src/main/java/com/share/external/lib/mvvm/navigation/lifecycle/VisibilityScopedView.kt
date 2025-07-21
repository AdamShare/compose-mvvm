package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.share.external.lib.compose.context.viewVisibilityObserver
import com.share.external.lib.mvvm.base.View
import com.share.external.lib.mvvm.base.ViewProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

@Stable
class VisibilityScopedView<V>(
    private val scopeFactory: () -> CoroutineScope,
    private val viewProvider: V,
): View where V: ViewProvider {
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
                    currentView.value = viewProvider.onViewAppear(scope)
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