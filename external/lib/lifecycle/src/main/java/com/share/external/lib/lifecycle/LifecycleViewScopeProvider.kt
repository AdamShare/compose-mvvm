package com.share.external.lib.lifecycle

import androidx.savedstate.SavedState
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.View
import com.share.external.lib.view.ViewScopeProvider
import kotlinx.coroutines.CoroutineScope

/**
 * A [ViewScopeProvider] that provides AndroidX lifecycle integration for navigation destinations.
 *
 * This class extends [ViewScopeProvider] to add [DefaultViewModelStoreOwner] support, enabling
 * views managed by navigation components to use AndroidX ViewModels, SavedStateHandle, and
 * lifecycle-aware components.
 *
 * ### Features
 * - Wraps view content with [DefaultViewModelStoreOwner.LocalOwnersProvider] for proper scoping
 * - Automatically clears the [DefaultViewModelStoreOwner] when cancelled, releasing ViewModels
 * - Supports saved state restoration across process death via [SavedState]
 *
 * ### Usage
 * This is typically used as a factory parameter for navigation stacks:
 * ```kotlin
 * val viewScopeProviderFactory = ViewScopeProvider.Factory { name, onViewAppear, scope ->
 *     LifecycleViewScopeProvider(
 *         name = name,
 *         onViewAppear = onViewAppear,
 *         savedState = null, // or restored state
 *         scope = scope
 *     )
 * }
 * ```
 *
 * @see DefaultViewModelStoreOwner for the underlying lifecycle owner implementation
 * @see ViewScopeProvider for the base provider interface
 */
class LifecycleViewScopeProvider private constructor(
    name: String,
    onViewAppear: (CoroutineScope) -> View,
    scope: ManagedCoroutineScope,
    private val viewModelStoreOwner: DefaultViewModelStoreOwner,
): ViewScopeProvider(
    name = name,
    onViewAppear = {
        val content = onViewAppear(it).content
        View {
            viewModelStoreOwner.LocalOwnersProvider(content = content)
        }
    },
    scope = scope,
) {
    constructor(
        name: String,
        onViewAppear: (CoroutineScope) -> View,
        savedState: SavedState?,
        scope: ManagedCoroutineScope,
    ): this(
        name = name,
        onViewAppear = onViewAppear,
        scope = scope,
        viewModelStoreOwner = DefaultViewModelStoreOwner(savedState = savedState),
    )

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        super.cancel(awaitChildrenComplete, message)
        viewModelStoreOwner.clear()
    }
}
