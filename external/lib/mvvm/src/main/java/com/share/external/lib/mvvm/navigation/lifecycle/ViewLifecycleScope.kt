package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Composable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ViewLifecycleScope: ManagedCoroutineScope {
    val viewVisible: StateFlow<Boolean>
}

internal open class ViewLifecycleScopeImpl(
    scope: ManagedCoroutineScope,
): ViewLifecycleScope, ManagedCoroutineScope by scope {
    private val visibility: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val viewVisible: StateFlow<Boolean> get() = visibility

    fun onVisible() {
        visibility.value = true
    }

    fun onHidden() {
        visibility.value = false
    }
}

@Composable
internal fun ViewLifecycleScopeImpl.ObserveViewVisibility() {
    ViewVisibilityObserver(
        onVisible = ::onVisible,
        onHidden = ::onHidden
    )
}