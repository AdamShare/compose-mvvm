package com.share.external.lib.mvvm.navigation.lifecycle

import androidx.compose.runtime.Composable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface representing a view's logical lifecycle, exposing [viewAppearanceEvents] to observe when a view becomes
 * visible or hidden.
 *
 * This is useful for triggering lifecycle-aware behavior at the view level, independent of activity or fragment
 * lifecycle events.
 */
interface ViewLifecycle {
    val viewAppearanceEvents: ViewAppearanceEvents
}

/**
 * Concrete implementation of [ViewLifecycle] that also acts as a [ManagedCoroutineScope].
 *
 * Provides a [viewAppearanceEvents] stream which emits visibility state of the view. Typically scoped to a navigation
 * back stack entry or similar view-level construct.
 *
 * This scope continues across configuration/context changes and only cancels when the view is removed from the back
 * stack or the app moves to the background.
 */
internal open class ViewLifecycleScope(actual: ManagedCoroutineScope) : ViewLifecycle, ManagedCoroutineScope by actual {
    override val viewAppearanceEvents = ViewAppearanceEventsImpl()
}

/**
 * Concrete implementation of [ViewAppearanceEvents] backed by a [MutableStateFlow].
 *
 * Use [onVisible] and [onHidden] to control visibility state manually. Emits true when the view is visible and false
 * when hidden.
 *
 * These states are not tied to activity lifecycle directly but represent view-level visibility based on navigation
 * state and app foreground/background events.
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
internal class ViewAppearanceEventsImpl(private val visibility: MutableStateFlow<Boolean> = MutableStateFlow(false)) :
    ViewAppearanceEvents, StateFlow<Boolean> by visibility {
    fun onVisible() {
        visibility.value = true
    }

    fun onHidden() {
        visibility.value = false
    }
}

/**
 * Composable that observes Compose view lifecycle and forwards visibility changes to this [ViewAppearanceEventsImpl]
 * instance.
 *
 * Automatically calls [onVisible] and [onHidden] when the view is shown or hidden.
 *
 * Note: This tracks logical view appearance, not tied to activity recreation or recomposition.
 */
@Composable
internal fun ViewAppearanceEventsImpl.ObserveViewVisibility() {
    viewVisibilityObserver(onVisible = ::onVisible, onHidden = ::onHidden)
}
