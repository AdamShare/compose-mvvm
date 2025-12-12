package com.share.external.lib.view.context

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Provides information about the view's visibility context within the application.
 *
 * This interface bridges the gap between Android's Activity lifecycle and Compose's composition
 * lifecycle, enabling views to respond to app-level visibility changes (foreground/background)
 * independently of their position in the composition tree.
 *
 * ### Foreground State
 * The [foregroundStateFlow] emits `true` when the app is visible to the user (typically when
 * the Activity is in RESUMED state) and `false` when backgrounded (PAUSED or STOPPED).
 *
 * ### Configuration Changes
 * The [isChangingConfigurations] flag indicates whether the current context is being destroyed
 * due to a configuration change (e.g., rotation). Views can use this to avoid cleanup actions
 * that would lose state during recreation.
 *
 * @see LocalViewContext for accessing the current context in Compose
 * @see viewVisibilityObserver for using this context to track view visibility
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
interface ViewContext {
    /**
     * Emits the current foreground state of the application.
     *
     * - `true`: The app is in the foreground and visible to the user
     * - `false`: The app is in the background or not visible
     */
    val foregroundStateFlow: StateFlow<Boolean>

    /**
     * Indicates whether the hosting Activity is being destroyed due to a configuration change.
     *
     * When `true`, views should avoid cleanup actions that would lose state, as the view
     * will be recreated with the same state after the configuration change completes.
     */
    val isChangingConfigurations: Boolean get() = false

    companion object {
        /**
         * An empty [ViewContext] that always reports foreground state as `true`.
         *
         * Used as the default value for [LocalViewContext] when no context is provided.
         */
        val EMPTY = object : ViewContext {
            override val foregroundStateFlow: StateFlow<Boolean> = MutableStateFlow(true).asStateFlow()
        }
    }
}

/**
 * Composition local providing the current [ViewContext].
 *
 * Access this in composables to observe foreground state or check for configuration changes:
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val viewContext = LocalViewContext.current
 *     LaunchedEffect(viewContext) {
 *         viewContext.foregroundStateFlow.collect { isForeground ->
 *             if (isForeground) startUpdates() else stopUpdates()
 *         }
 *     }
 * }
 * ```
 */
val LocalViewContext = staticCompositionLocalOf { ViewContext.EMPTY }
