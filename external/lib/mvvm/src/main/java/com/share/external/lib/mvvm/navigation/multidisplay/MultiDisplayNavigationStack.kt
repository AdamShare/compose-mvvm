package com.share.external.lib.mvvm.navigation.multidisplay

import androidx.compose.runtime.Stable
import com.share.external.foundation.coroutines.ManagedCancellable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.content.NavigationKey
import com.share.external.lib.mvvm.navigation.content.ViewPresentation
import com.share.external.lib.mvvm.navigation.scope.ViewScopeProvider
import com.share.external.lib.mvvm.navigation.stack.ManagedCoroutineScopeStack
import com.share.external.lib.mvvm.navigation.stack.NavigationStack

class MultiDisplayNavigationStack<V, D: Display>(
    rootScope: ManagedCoroutineScope,
    viewScopeProviderFactory: ViewScopeProvider.Factory = ViewScopeProvider.Factory.Default,
    initialStack: (NavigationStack<MultiDisplayViewProvider<V, D>>) -> Unit = {},
): ManagedCoroutineScopeStack<MultiDisplayViewProvider<V, D>, MultiViewScopeProvider<D>>(
    rootScope = rootScope,
    entryFactory = { key, scope, viewProvider ->
        MultiViewProviderImpl(
            navigationKey = key,
            scope = scope,
            viewProvider = viewProvider,
            viewScopeProviderFactory = viewScopeProviderFactory,
        )
    },
    initialStack = initialStack,
) where V: ViewProvider, V: ViewPresentation

@Stable
interface MultiDisplayViewProvider<V, D: Display> {
    fun get(display: D): V?
}

interface Display {
    val isDefault: Boolean
}

interface MultiViewScopeProvider<D: Display>: ManagedCancellable {
    fun viewProvider(display: D): ViewProvider?
    fun scopedViewProvider(display: D): ViewScopeProvider?
}

internal class MultiViewProviderImpl<V: ViewProvider, D: Display>(
    val navigationKey: NavigationKey,
    val scope: ManagedCoroutineScope,
    val viewProvider: MultiDisplayViewProvider<V, D>,
    val viewScopeProviderFactory: ViewScopeProvider.Factory,
): MultiViewScopeProvider<D> {
    private val scopedViewProviders = mutableMapOf<D, ViewScopeProvider?>()

    override fun viewProvider(display: D): V? = viewProvider.get(display)

    override fun scopedViewProvider(display: D) = scopedViewProviders.getOrPut(display) {
        viewProvider(display)?.let {
            viewScopeProviderFactory(
                name = navigationKey.name,
                onViewAppear = it::onViewAppear,
                scope = scope
            )
        }
    }

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        scopedViewProviders.values.forEach { it?.cancel(awaitChildrenComplete, message) }
    }
}
