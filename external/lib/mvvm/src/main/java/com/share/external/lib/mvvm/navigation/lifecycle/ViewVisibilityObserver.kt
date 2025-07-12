package com.share.external.lib.mvvm.navigation.lifecycle

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.share.external.lib.mvvm.activity.findActivity

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
@SuppressLint("RememberReturnType")
@Composable
fun <R> viewVisibilityObserver(onVisible: () -> R, onHidden: () -> Unit): R {
    val activity = findActivity<Activity>()
    var isVisible by remember {
        mutableStateOf(true)
    }
    val visible = remember {
        // Run on initial composition as DisposableEffect is delayed.
        onVisible()
    }

    // Track when the view is composed and disposed
    DisposableEffect(Unit) {
        onDispose {
            if (!activity.isChangingConfigurations) {
                onHidden()
            }
        }
    }

    // Track lifecycle transitions (resume/pause)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (!isVisible) {
                        isVisible = true
                        onVisible()
                    }
                }
                Lifecycle.Event.ON_PAUSE ->
                    if (!activity.isChangingConfigurations && isVisible) {
                        isVisible = false
                        onHidden()
                    }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return visible
}
