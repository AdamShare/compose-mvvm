package com.share.external.lib.navigation.switcher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewKey
import com.share.external.lib.view.ViewProvider
import com.share.external.lib.view.ViewScopeProvider
import kotlinx.coroutines.Dispatchers

/**
 * Base implementation of [ViewSwitcher] that handles common functionality.
 *
 * Subclasses define the storage and lifecycle strategy for view scopes.
 *
 * @param K The type of navigation key used to identify different views.
 * @param scope The parent [ManagedCoroutineScope] that owns this switcher.
 * @param defaultKey The initially selected key, or null if nothing is selected.
 * @param viewScopeProviderFactory Factory for creating [ViewScopeProvider] instances.
 */
abstract class BaseViewSwitcher<K : ViewKey>(
    protected val scope: ManagedCoroutineScope,
    defaultKey: K? = null,
    protected val viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
) : ViewSwitcher<K> {

    override var selected: K? by mutableStateOf(defaultKey)
        protected set

    init {
        scope.invokeOnCompletion(Dispatchers.Main.immediate) {
            onParentScopeCompleted()
        }
    }

    /**
     * Called when the parent scope completes. Subclasses should clean up all resources.
     */
    protected abstract fun onParentScopeCompleted()

    /**
     * Creates a new [ViewScope] for the given key.
     */
    protected fun createViewScope(
        key: K,
        content: (K, ManagedCoroutineScope) -> ViewProvider
    ): ViewScope<K> {
        val childScope = scope.childManagedScope(key.name)
        return ViewScope(
            key = key,
            scope = viewScopeProviderFactory(
                name = key.name,
                onViewAppear = content(key, childScope)::onViewAppear,
                scope = childScope,
            ),
        )
    }

    /**
     * Holds a key and its associated [ViewScopeProvider].
     */
    protected class ViewScope<K : ViewKey>(
        val key: K,
        val scope: ViewScopeProvider,
    ) {
        fun cancel(message: String) {
            scope.cancel(awaitChildrenComplete = false, message = message)
        }
    }
}
