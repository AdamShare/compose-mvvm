package com.share.external.lib.view

import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope

/**
 * A factory for creating [View] instances with a visibility-scoped [CoroutineScope].
 *
 * This interface follows a lifecycle pattern where [onViewAppear] is called when a view
 * becomes visible and the provided scope is cancelled when the view is hidden. This enables
 * automatic cleanup of async operations tied to view visibility rather than Android lifecycle.
 *
 * ### Visibility vs Android Lifecycle
 * The scope provided to [onViewAppear] is independent of Android's Activity/Fragment lifecycle.
 * It tracks when the view is actually visible in the composition, which is more granular than
 * lifecycle states. For example:
 * - A view pushed onto a navigation stack becomes "hidden" when another view is pushed on top
 * - The scope cancels even though the Activity remains in RESUMED state
 *
 * ### Usage
 * ```kotlin
 * class MyViewProvider : ViewProvider {
 *     override fun onViewAppear(scope: CoroutineScope): View {
 *         scope.launch {
 *             // This coroutine cancels when the view is hidden
 *             analyticsTracker.trackScreenView("MyScreen")
 *         }
 *         return View { MyScreenContent() }
 *     }
 * }
 * ```
 *
 * @see VisibilityScopedView for the composable wrapper that manages visibility callbacks
 */
@Stable
fun interface ViewProvider {
    /**
     * Creates a [View] when it becomes visible, providing a [CoroutineScope] for async operations.
     *
     * This method is called once for the duration of a view's appearance (not to be confused with
     * Android's [android.app.Activity]/[android.view.View] lifecycle). The scope cancels when
     * the view is hidden and a new scope is provided on each subsequent appearance.
     *
     * @param scope A [CoroutineScope] that remains active while the view is visible.
     *   Cancels automatically when the view is hidden or removed from composition.
     * @return The [View] to be displayed.
     */
    fun onViewAppear(scope: CoroutineScope): View
}
