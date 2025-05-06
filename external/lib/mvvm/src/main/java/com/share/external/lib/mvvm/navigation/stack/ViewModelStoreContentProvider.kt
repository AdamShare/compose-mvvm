package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.lifecycle.DefaultViewModelStoreOwner
import com.share.external.lib.mvvm.navigation.lifecycle.LocalOwnersProvider

/**
 * Bridges Compose owners (ViewModelStoreOwner, SaveableStateHolder, etc.) with
 * the actual [content] so every screen gets proper lifecycle ownership.
 */
@Immutable
open class ViewModelStoreContentProvider<V>(
    val content: V,
    private val scope: ManagedCoroutineScope,
): ManagedCoroutineScope by scope {
    private val owner = DefaultViewModelStoreOwner()

    override fun cancel(awaitChildrenComplete: Boolean, message: String) {
        owner.clear()
        scope.cancel(
            awaitChildrenComplete = awaitChildrenComplete,
            message = message
        )
    }

    @Composable
    fun LocalOwnersProvider(
        saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
        content: @Composable () -> Unit
    ) {
        owner.LocalOwnersProvider(saveableStateHolder, content)
    }
}
