package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.lifecycle.DefaultViewModelStoreOwner
import com.share.external.lib.mvvm.navigation.lifecycle.LocalOwnersProvider
import com.share.external.lib.mvvm.navigation.lifecycle.ObserveViewVisibility
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycleScope
import com.share.external.lib.mvvm.navigation.lifecycle.ViewLifecycleScopeImpl
import com.share.external.lib.mvvm.navigation.lifecycle.ViewVisibilityObserver

interface ViewModelStoreContentProvider<V>: ManagedCoroutineScope {
    val view: V

    @Composable
    fun LocalOwnersProvider(
        saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
        content: @Composable () -> Unit
    )
}

/**
 * Bridges Compose owners (ViewModelStoreOwner, SaveableStateHolder, etc.) with
 * the actual [view] so every screen gets proper lifecycle ownership.
 */
@Immutable
internal open class ViewModelStoreContentProviderImpl<V>(
    override val view: V,
    private val scope: ViewLifecycleScopeImpl,
): ViewModelStoreContentProvider<V>, ManagedCoroutineScope by scope {
    private val owner = DefaultViewModelStoreOwner()

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        owner.clear()
        scope.cancel(
            awaitChildrenComplete = awaitChildrenComplete,
            message = message
        )
    }

    @Composable
    override fun LocalOwnersProvider(
        saveableStateHolder: SaveableStateHolder,
        content: @Composable () -> Unit
    ) {
        owner.LocalOwnersProvider(saveableStateHolder) {
            scope.ObserveViewVisibility()
            content()
        }
    }
}
