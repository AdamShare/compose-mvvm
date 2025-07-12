package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.View

@Stable
class VisibilityScopedView<V>(
    private val parentScope: ManagedCoroutineScope,
    private val viewProvider: V,
): View where V: ViewProvider {
    private var currentScope: ViewManagedCoroutineScope? = null
    private val currentView = mutableStateOf<View?>(null)

    override val content: @Composable () -> Unit = {
        viewVisibilityObserver(
            onVisible = {
                currentScope?.run {
                    cancel("View became visible again without previous scope canceled. (unexpected state)")
                }
                val scope = ViewManagedCoroutineScope(parentScope)
                currentScope = scope
                currentView.value = viewProvider.create(scope)
                currentView
            },
            onHidden = {
                currentScope?.cancel("View hidden")
                currentScope = null
            }
        ).value?.content?.invoke()
    }
}