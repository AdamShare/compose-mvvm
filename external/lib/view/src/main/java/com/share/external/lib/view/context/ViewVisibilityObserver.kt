package com.share.external.lib.view.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.drop

/**
 * Observes the visibility lifecycle of a Composable view and triggers callbacks when it becomes visible or hidden to
 * the user.
 *
 * This utility is useful for tracking the appearance and disappearance of a screen or UI element within the Compose
 * hierarchy, particularly when using custom navigation stacks or embedded views.
 *
 * Behavior:
 * - Invokes [onVisible] immediately on first composition.
 * - Invokes [onHidden] when the composable leaves the composition, unless the activity is changing configurations
 *   (e.g., orientation change).
 * - Listens to lifecycle transitions and invokes:
 *     - [onVisible] on `ON_RESUME`
 *     - [onHidden] on `ON_PAUSE` if not caused by a configuration change
 *
 * This approach ensures that visibility is tracked both at the composition level (via `DisposableEffect`) and the
 * activity lifecycle level (via `LifecycleEventObserver`), making it suitable for scenarios where precise
 * appearance/disappearance detection is needed (e.g., analytics, logging, resource management).
 *
 * @param onVisible Called when the view becomes visible (initial composition or `ON_RESUME`).
 * @param onHidden Called when the view is no longer visible (disposed or `ON_PAUSE`, excluding configuration changes).
 */
@Suppress("RememberReturnType")
@Composable
fun <R> viewVisibilityObserver(onVisible: () -> R, onHidden: () -> Unit): R {
    val visible = remember {
        // Run on initial composition as DisposableEffect is delayed.
        onVisible()
    }

    val foregroundState = LocalViewContext.current

    // Track when the view is composed and disposed
    DisposableEffect(Unit) {
        onDispose {
            if (!foregroundState.isChangingConfigurations) {
                onHidden()
            }
        }
    }

    // Track foreground
    LaunchedEffect(foregroundState) {
        foregroundState.foregroundStateFlow.drop(1).collect {
            if (it) {
                onVisible()
            } else {
                onHidden()
            }
        }
    }
    return visible
}
