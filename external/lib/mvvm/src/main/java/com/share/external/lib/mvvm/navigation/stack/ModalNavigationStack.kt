package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Stable
import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.mvvm.navigation.scope.ViewScopeProvider

class ModalNavigationStack<V>(
    rootScope: ManagedCoroutineScope,
    viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
    initialStack: (NavigationStack<V>) -> Unit = {},
): ManagedCoroutineScopeStack<V, NavigationStackEntryViewProvider>(
    rootScope = rootScope,
    entryFactory = { key, scope, viewProvider ->
        NavigationStackEntryViewProviderImpl(
            navigationKey = key,
            viewProvider = viewProvider,
            scopedViewProvider = viewScopeProviderFactory(
                name = key.name,
                onViewAppear = viewProvider::onViewAppear,
                scope = scope
            ),
        )
    },
    initialStack = initialStack,
) where V: ViewProvider, V: ViewPresentation
