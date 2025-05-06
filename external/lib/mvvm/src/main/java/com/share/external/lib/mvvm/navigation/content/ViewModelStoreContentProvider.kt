package com.share.external.lib.mvvm.navigation.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.lib.mvvm.navigation.lifecycle.DefaultViewModelStoreOwner
import com.share.external.lib.mvvm.navigation.lifecycle.LocalOwnersProvider

@Immutable
open class ViewModelStoreContentProvider<K, V>(
    val key: K,
    val content: V,
) {
    private val owner = DefaultViewModelStoreOwner()

    open fun clear() {
        owner.clear()
    }

    @Composable
    fun LocalOwnersProvider(
        saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
        content: @Composable () -> Unit
    ) {
        owner.LocalOwnersProvider(saveableStateHolder, content)
    }
}
