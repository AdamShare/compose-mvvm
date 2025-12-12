package com.share.external.lib.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.share.external.lib.view.context.viewVisibilityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * A [View] that manages a [CoroutineScope] tied to its visibility state.
 *
 * When this view becomes visible (enters composition and the app is in foreground), a new
 * [CoroutineScope] is created via [scopeFactory] and passed to [onViewAppear]. When the view
 * becomes hidden (leaves composition or app goes to background), the scope is cancelled.
 *
 * ### Visibility Tracking
 * Visibility is determined by [viewVisibilityObserver], which tracks both:
 * - Composition state (whether the view is in the tree)
 * - Foreground state (whether the app is visible to the user)
 *
 * ### Scope Lifecycle
 * - **Visible**: A new scope is created and [onViewAppear] is called to create the view
 * - **Hidden**: The scope is cancelled with message "View hidden"
 * - **Re-visible**: A fresh scope is created (previous scope is not reused)
 *
 * ### Usage
 * ```kotlin
 * val view = VisibilityScopedView(
 *     scopeFactory = { CoroutineScope(Dispatchers.Main + SupervisorJob()) },
 *     onViewAppear = { scope ->
 *         scope.launch { loadData() }
 *         View { MyContent() }
 *     }
 * )
 * ```
 *
 * @param scopeFactory Factory that creates a new [CoroutineScope] each time the view becomes visible.
 * @param onViewAppear Called when the view becomes visible, receiving the newly created scope.
 *   Returns the [View] to display while visible.
 */
@Stable
class VisibilityScopedView(
    private val scopeFactory: () -> CoroutineScope,
    private val onViewAppear: (CoroutineScope) -> View,
): View {
    /**
     * Convenience constructor that wraps a [ViewProvider].
     *
     * @param scopeFactory Factory that creates a new [CoroutineScope] each time the view becomes visible.
     * @param viewProvider The [ViewProvider] whose [ViewProvider.onViewAppear] will be called on visibility.
     */
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
