package com.share.external.lib.mvvm.navigation.lifecycle

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

/**
 * A [StateFlow] representing whether the view is currently visible.
 *
 * Emits `true` when the view is visible, `false` when hidden. Visibility here reflects **logical appearance** from a
 * navigation or view-layer perspective, not Compose recomposition or Android configuration changes.
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class) interface ViewAppearanceEvents : StateFlow<Boolean>

/**
 * Binds a [ViewAppearanceListener] to [ViewAppearanceEvents] within the given [scope].
 *
 * The [listener] will be triggered when the view becomes visible and will be cancelled when hidden.
 *
 * Unlike typical Android lifecycle callbacks, these appearance events:
 * - **Ignore context changes**, such as configuration changes that recreate the activity.
 * - **Track logical view visibility**, not composable recomposition or activity lifecycle events.
 * - **Cancel** only when the view is removed from the navigation stack or the app is backgrounded.
 */
suspend fun ViewAppearanceEvents.onViewAppear(action: suspend () -> Unit) {
    collectLatest {
        if (it) {
            action()
        }
    }
}

/**
 * Suspends until the view becomes visible.
 *
 * This function waits for the first `true` emission from [ViewAppearanceEvents], which indicates that the view has
 * become logically visible.
 *
 * This is useful when certain actions should only occur once the view is presented to the user, independent of Compose
 * recomposition or Android lifecycle events.
 *
 * Like other appearance events:
 * - Visibility is based on navigation state or manual signals via [ViewAppearanceEventsImpl].
 * - The suspension will continue across configuration changes or recompositions.
 * - It only resumes when the view enters a visible state.
 */
suspend fun ViewAppearanceEvents.awaitViewAppear() {
    first { it }
}
