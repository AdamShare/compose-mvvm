package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.lifecycle.VisibilityScopedView

/**
 * A container that bridges a [view] and its associated [ViewModelStore], [CoroutineScope], and [SaveableStateHolder]
 * into a reusable composition host.
 *
 * This interface ensures that each screen or modal view receives correct lifecycle ownership and state management
 * support within a Compose navigation context.
 *
 * Implementations must ensure:
 * - A retained [CoroutineScope] for view-related logic.
 * - Correct scoping of [ViewModelStoreOwner] and [SaveableStateHolder] via [LocalOwnersProvider].
 *
 * @param V The view type being hosted.
 */
interface ViewModelStoreContentProvider<V> : ManagedCoroutineScope {
    /** The view instance that this provider is managing. */
    val view: V

    /**
     * Provides the necessary local owners ([ViewModelStoreOwner], [SaveableStateHolder], etc.) and lifecycle visibility
     * context for [view] composition.
     *
     * This should be called from within a composable scope to wrap the [view]'s UI logic.
     *
     * @param saveableStateHolder The state holder used to preserve UI state across recompositions.
     * @param content The composable content representing the [view]'s UI.
     */
    @Composable
    fun LocalOwnersProvider(
        saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
        content: @Composable () -> Unit,
    )
}
