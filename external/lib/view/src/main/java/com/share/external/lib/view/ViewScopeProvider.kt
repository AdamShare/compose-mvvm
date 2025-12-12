package com.share.external.lib.view

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference
import java.util.UUID

/**
 * Manages a view's lifecycle scope and its integration with Compose's saveable state system.
 *
 * This class combines:
 * - A [ManagedCoroutineScope] for coroutine lifecycle management
 * - A [VisibilityScopedView] for visibility-aware view creation
 * - Integration with [SaveableStateHolder] for state preservation across configuration changes
 *
 * ### State Preservation
 * When associated with a [SaveableStateHolder] via [setSaveableStateHolder], the provider
 * automatically removes saved state when cancelled, preventing memory leaks from orphaned state.
 *
 * ### Usage in Navigation
 * Used by navigation components (like [ViewSwitcher][com.share.external.lib.navigation.switcher.ViewSwitcher])
 * to manage view instances that need both coroutine scope management and compose state preservation.
 *
 * @param name A human-readable identifier for debugging and logging.
 * @param onViewAppear Factory function called when the view becomes visible.
 * @param scope The [ManagedCoroutineScope] that owns this provider's lifecycle.
 */
@Immutable
open class ViewScopeProvider(
    val name: String,
    onViewAppear: (CoroutineScope) -> View,
    private val scope: ManagedCoroutineScope,
) : ManagedCancellable {
    /**
     * Unique identifier for this provider instance, used as a key for [SaveableStateHolder].
     */
    val id: UUID = UUID.randomUUID()

    /**
     * The visibility-scoped view managed by this provider.
     */
    val view = VisibilityScopedView(
        onViewAppear = onViewAppear,
        scopeFactory = { scope.create(name = name + "Visibility", context = Dispatchers.Main.immediate) },
    )

    private var saveableStateHolderRef: WeakReference<SaveableStateHolder>? = null

    /**
     * Associates this provider with a [SaveableStateHolder] for state preservation.
     *
     * When [cancel] is called, any saved state associated with this provider's [id]
     * will be removed from the holder.
     *
     * @param saveableStateHolder The holder to associate with this provider.
     */
    fun setSaveableStateHolder(saveableStateHolder: SaveableStateHolder) {
        saveableStateHolderRef = WeakReference(saveableStateHolder)
    }

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        saveableStateHolderRef?.run {
            get()?.removeState(id)
            clear()
            saveableStateHolderRef = null
        }
        scope.cancel(awaitChildrenComplete = awaitChildrenComplete, message = message)
    }

    /**
     * Factory for creating [ViewScopeProvider] instances.
     */
    fun interface Factory {
        /**
         * Creates a new [ViewScopeProvider].
         *
         * @param name A human-readable identifier for the provider.
         * @param onViewAppear Factory function called when the view becomes visible.
         * @param scope The [ManagedCoroutineScope] that owns the provider's lifecycle.
         * @return A new [ViewScopeProvider] instance.
         */
        operator fun invoke(
            name: String,
            onViewAppear: (CoroutineScope) -> View,
            scope: ManagedCoroutineScope
        ): ViewScopeProvider

        companion object {
            /**
             * Default factory that creates standard [ViewScopeProvider] instances.
             */
            val Default = Factory { name, onViewAppear, scope ->
                ViewScopeProvider(name = name, onViewAppear = onViewAppear, scope = scope)
            }
        }
    }
}
