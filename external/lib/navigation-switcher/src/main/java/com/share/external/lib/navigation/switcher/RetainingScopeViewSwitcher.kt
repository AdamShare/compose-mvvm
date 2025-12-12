package com.share.external.lib.navigation.switcher

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.view.ViewKey
import com.share.external.lib.view.ViewProvider
import com.share.external.lib.view.ViewScopeProvider

/**
 * A [ViewSwitcher] implementation that retains the state of all previously selected items.
 *
 * Unlike [SingleScopeViewSwitcher] which cancels the previous scope when switching,
 * this implementation maintains a map of all created providers, allowing users to
 * switch back to a previously selected route without losing its state.
 *
 * This is useful for tab-based navigation where each tab should retain its state
 * (scroll position, form data, navigation stack, etc.) when switching between tabs.
 *
 * @param K The type of navigation key used to identify different views.
 * @param scope The parent [ManagedCoroutineScope] that owns this switcher.
 * @param defaultKey The initially selected key, or null if nothing is selected.
 * @param viewScopeProviderFactory Factory for creating [ViewScopeProvider] instances.
 */
class RetainingScopeViewSwitcher<K : ViewKey>(
    scope: ManagedCoroutineScope,
    defaultKey: K? = null,
    viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
) : BaseViewSwitcher<K>(scope, defaultKey, viewScopeProviderFactory) {

    private val providers = mutableMapOf<K, ViewScope<K>>()

    override fun onSelect(key: K?) {
        selected = key
    }

    override fun getOrCreate(content: (K, ManagedCoroutineScope) -> ViewProvider): ViewScopeProvider? {
        val selectedKey = selected ?: return null
        return providers.getOrPut(selectedKey) {
            createViewScope(selectedKey, content)
        }.scope
    }

    override fun onParentScopeCompleted() {
        providers.values.forEach { viewScope ->
            viewScope.cancel(message = "Parent scope completed: $scope")
        }
        providers.clear()
    }
}
