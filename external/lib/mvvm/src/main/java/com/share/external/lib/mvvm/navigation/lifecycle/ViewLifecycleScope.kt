package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Composable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * A coroutine scope tied to the logical lifecycle of a view within a navigation context.
 *
 * - The scope survives activity recreation (e.g., due to rotation).
 * - It is cancelled only when the view is removed from the screen entirely or the app is backgrounded.
 * - Visibility changes are communicated via [viewAppearanceEvents].
 */
interface ViewLifecycleScope : ManagedCoroutineScope {
    val viewAppearanceEvents: ViewAppearanceEvents
}

/**
 * Default implementation of [ViewLifecycleScope], delegating coroutine management
 * to a provided [ManagedCoroutineScope] while managing view appearance events internally.
 */
internal open class ViewLifecycleScopeImpl(
    actual: ManagedCoroutineScope,
) : ViewLifecycleScope, ManagedCoroutineScope by actual {
    override val viewAppearanceEvents = ViewAppearanceEventsImpl()
}

/**
 * Concrete implementation of [ViewAppearanceEvents] backed by a [MutableStateFlow].
 *
 * Use [onVisible] and [onHidden] to control visibility state manually.
 * Emits true when the view is visible and false when hidden.
 *
 * These states are not tied to activity lifecycle directly but represent view-level
 * visibility based on navigation state and app foreground/background events.
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
internal class ViewAppearanceEventsImpl(
    private val visibility: MutableStateFlow<Boolean> = MutableStateFlow(false)
) : ViewAppearanceEvents, StateFlow<Boolean> by visibility {
    fun onVisible() {
        visibility.value = true
    }

    fun onHidden() {
        visibility.value = false
    }
}

/**
 * Composable that observes Compose view lifecycle and forwards visibility changes
 * to this [ViewAppearanceEventsImpl] instance.
 *
 * Automatically calls [onVisible] and [onHidden] when the view is shown or hidden.
 *
 * Note: This tracks logical view appearance, not tied to activity recreation or recomposition.
 */
@Composable
internal fun ViewAppearanceEventsImpl.ObserveViewVisibility() {
    ViewVisibilityObserver(
        onVisible = ::onVisible,
        onHidden = ::onHidden
    )
}