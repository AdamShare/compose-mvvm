package com.share.external.lib.mvvm.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.lib.mvvm.navigation.lifecycle.LocalDefaultViewModelStoreOwner
import com.share.external.lib.mvvm.navigation.stack.NavigationStackScope
import com.share.external.lib.mvvm.navigation.stack.ViewModelNavigationStack
import kotlinx.coroutines.CoroutineScope

open class NavigationController<K, V : ComposableProvider>(
    scope: CoroutineScope,
) : ViewModelNavigationStack<K, V>(scope) {
    @Composable
    fun Content(defaultContent: @Composable (() -> Unit)? = null) {
        val saveableStateHolder = rememberSaveableStateHolder()

        currentProvider?.apply {
            LocalDefaultViewModelStoreOwner(
                owner = owner,
                saveableStateHolder = saveableStateHolder,
            ) {
                content.Content()
            }

            BackHandler {
                pop()
            }
        } ?: defaultContent?.invoke()
    }

    override fun push(key: K, content: (NavigationStackScope<K, V>) -> V) {
        TODO("Not yet implemented")
    }
}
