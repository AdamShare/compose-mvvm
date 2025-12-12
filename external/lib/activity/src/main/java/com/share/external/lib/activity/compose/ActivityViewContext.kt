package com.share.external.lib.activity.compose

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.share.external.lib.view.context.ViewContext
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Creates and remembers an [ActivityViewContext] tied to this Activity's lifecycle.
 *
 * The returned context tracks:
 * - Foreground state (ON_RESUME → visible, ON_PAUSE → not visible)
 * - Configuration change detection to prevent premature cleanup
 *
 * @return A [ViewContext] that reflects this Activity's visibility state.
 */
@Composable
fun ComponentActivity.rememberActivityViewContext(): ViewContext {
    return remember(this) {
        ActivityViewContext(
            activity = this,
        )
    }
}

/**
 * A [ViewContext] implementation that bridges Activity lifecycle events to view visibility.
 *
 * This class observes the Activity's lifecycle and updates [foregroundStateFlow] accordingly:
 * - ON_RESUME: Sets foreground state to `true`
 * - ON_PAUSE: Sets foreground state to `false` (unless changing configurations)
 * - ON_DESTROY: Removes the lifecycle observer
 *
 * @param activity The Activity to observe.
 * @param isVisible Initial visibility state, defaults to `true`.
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
class ActivityViewContext(
    private val activity: ComponentActivity,
    private val isVisible: MutableStateFlow<Boolean> = MutableStateFlow(true)
): ViewContext, LifecycleEventObserver {
    override val foregroundStateFlow: MutableStateFlow<Boolean> = isVisible
    override val isChangingConfigurations: Boolean get() = activity.isChangingConfigurations

    init {
        activity.lifecycle.addObserver(observer = this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                activity.lifecycle.removeObserver(this)
            }
            Lifecycle.Event.ON_RESUME -> {
                isVisible.value = true
            }
            Lifecycle.Event.ON_PAUSE ->
                if (!isChangingConfigurations) {
                    isVisible.value = false
                }
            else -> Unit
        }
    }
}