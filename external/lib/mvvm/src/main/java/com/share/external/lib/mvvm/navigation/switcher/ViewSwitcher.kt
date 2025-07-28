package com.share.external.lib.mvvm.navigation.switcher

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.scope.ViewScopeProvider

interface ViewSwitcher<K : NavigationKey> {
    val selected: K?

    fun onSelect(key: K?)

    fun getOrCreate(content: (K, ManagedCoroutineScope) -> ViewProvider): ViewScopeProvider?
}
