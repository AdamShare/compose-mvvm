package com.share.external.lib.navigation.switcher

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewKey
import com.share.external.lib.view.ViewProvider
import com.share.external.lib.view.ViewScopeProvider

/**
 * A [ViewSwitcher] implementation that maintains only a single active scope at a time.
 *
 * When switching to a new key, the previous scope is cancelled. This is suitable for
 * navigation flows where you don't need to retain state when moving away from a view.
 *
 * @param K The type of navigation key used to identify different views.
 * @param scope The parent [ManagedCoroutineScope] that owns this switcher.
 * @param defaultKey The initially selected key, or null if nothing is selected.
 * @param viewScopeProviderFactory Factory for creating [ViewScopeProvider] instances.
 */
class SingleScopeViewSwitcher<K : ViewKey>(
    scope: ManagedCoroutineScope,
    defaultKey: K? = null,
    viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
) : BaseViewSwitcher<K>(scope, defaultKey, viewScopeProviderFactory) {

    private var currentProvider: ViewScope<K>? = null

    override fun onSelect(key: K?) {
        selected = key
        if (key == null) {
            currentProvider?.cancel(message = "selectedKey null")
            currentProvider = null
        }
    }

    override fun getOrCreate(content: (K, ManagedCoroutineScope) -> ViewProvider): ViewScopeProvider? {
        val selectedKey = selected ?: return null

        if (selectedKey != currentProvider?.key) {
            val previous = currentProvider
            currentProvider = createViewScope(selectedKey, content)
            previous?.cancel(message = "Switched to selected view: $selectedKey")
        }
        return currentProvider?.scope
    }

    override fun onParentScopeCompleted() {
        currentProvider?.cancel(message = "Parent scope completed: $scope")
        currentProvider = null
    }
}
