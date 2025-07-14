package com.share.external.lib.mvvm.navigation.switcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.share.external.foundation.coroutines.MainImmediateScope
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.base.ViewProvider
import com.share.external.lib.mvvm.navigation.stack.ViewProviderViewModelStoreContentProvider
import kotlinx.coroutines.launch

class ScopedViewSwitcher<K : NavigationKey>(private val scope: ManagedCoroutineScope, defaultKey: K? = null) :
    ViewSwitcher<K> {
    private var currentProvider: ViewScope<K>? = null

    override var selected: K? by mutableStateOf(defaultKey)

    init {
        scope.invokeOnCompletion {
            // Parent scope could complete off main thread.
            MainImmediateScope().launch { clear() }
        }
    }

    @Composable
    override fun Content(content: (K, ManagedCoroutineScope) -> ViewProvider) {
        val saveableStateHolder = rememberSaveableStateHolder()

        val selectedKey = selected
        if (selectedKey != null) {
            if (selectedKey != currentProvider?.key) {
                val scope = scope.childManagedScope(selectedKey.analyticsId)
                val previous = currentProvider
                currentProvider = ViewScope(
                    content = content(selectedKey, scope),
                    key = selectedKey,
                    scope = scope,
                )
                previous?.cancel(awaitChildrenComplete = false, message = "Switched to selected view: $selectedKey")
            }
        } else {
            currentProvider?.cancel(awaitChildrenComplete = false, message = "selectedKey null")
            currentProvider = null
        }

        currentProvider?.apply { LocalOwnersProvider(saveableStateHolder) { view.content() } }
    }

    private fun clear() {
        currentProvider?.cancel(awaitChildrenComplete = false, message = "Parent scope completed: $scope")
        currentProvider = null
    }

    private class ViewScope<K>(content: ViewProvider, val key: K, scope: ManagedCoroutineScope) :
        ViewProviderViewModelStoreContentProvider<ViewProvider>(viewProvider = content, scope = scope)
}
