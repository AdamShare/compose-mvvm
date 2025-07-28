package com.share.external.lib.mvvm.navigation.switcher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.scope.ViewScopeProvider
import kotlinx.coroutines.Dispatchers

class SingleScopeViewSwitcher<K : NavigationKey>(
    private val scope: ManagedCoroutineScope,
    defaultKey: K? = null,
    private val viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
) : ViewSwitcher<K> {
    private var currentProvider: ViewScope<K>? = null

    override var selected: K? by mutableStateOf(defaultKey)
        private set

    init {
        scope.invokeOnCompletion(Dispatchers.Main.immediate) {
            clear()
        }
    }

    override fun onSelect(key: K?) {
        selected = key
        if (key == null) {
            currentProvider?.scope?.cancel(
                awaitChildrenComplete = false,
                message = "selectedKey null"
            )
            currentProvider = null
        }
    }

    override fun getOrCreate(content: (K, ManagedCoroutineScope) -> ViewProvider): ViewScopeProvider? {
        val selectedKey = selected
        if (selectedKey != null) {
            if (selectedKey != currentProvider?.key) {
                val scope = scope.childManagedScope(selectedKey.name)
                val previous = currentProvider
                currentProvider = ViewScope(
                    key = selectedKey,
                    scope = viewScopeProviderFactory(
                        name = selectedKey.name,
                        onViewAppear = content(selectedKey, scope)::onViewAppear,
                        scope = scope,
                    ),
                )
                previous?.scope?.cancel(
                    awaitChildrenComplete = false,
                    message = "Switched to selected view: $selectedKey"
                )
            }
        }
        return currentProvider?.scope
    }

    private fun clear() {
        currentProvider?.scope?.cancel(
            awaitChildrenComplete = false,
            message = "Parent scope completed: $scope"
        )
        currentProvider = null
    }

    private class ViewScope<K: NavigationKey>(
        val key: K,
        val scope: ViewScopeProvider,
    )
}

