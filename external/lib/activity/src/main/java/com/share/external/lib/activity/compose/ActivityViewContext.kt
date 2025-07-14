package com.share.external.lib.activity.compose

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.share.external.lib.compose.context.ViewContext
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ComponentActivity.rememberActivityViewContext(): ViewContext {
    return remember(this) {
        ActivityViewContext(
            activity = this,
        )
    }
}

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