package com.share.external.lib.mvvm.navigation.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.lib.mvvm.navigation.lifecycle.LocalDefaultViewModelStoreOwner
import com.share.external.lib.mvvm.navigation.stack.NavigationStackScope
import com.share.external.lib.mvvm.navigation.stack.ViewModelNavigationStack
import kotlinx.coroutines.CoroutineScope

open class DialogNavigationController<K, V : DialogComposableProvider>(
    scope: CoroutineScope,
) : ViewModelNavigationStack<K, V>(scope),
    DialogNavigationStack<K, V> {

    @Composable
    fun PresentOver(
        backgroundContent: @Composable () -> Unit,
    ) {
        PresentOver({ it() }, backgroundContent)
    }

    @Composable
    fun PresentOver(
        wrapContent: @Composable (@Composable () -> Unit) -> Unit,
        backgroundContent: @Composable () -> Unit,
    ) {
        val currentContent = currentProvider?.content
        val properties = currentContent?.properties ?: DialogProperties()

        DialogContainer(
            onDismiss = ::removeAll,
            properties = properties,
            backgroundContent = backgroundContent,
            content = currentContent?.let {
                {
                    wrapContent {
                        DialogContent()
                    }
                }
            }
        )
    }

    @Composable
    private fun DialogContent() {
        val saveableStateHolder = rememberSaveableStateHolder()

        currentProvider?.apply {
            LocalDefaultViewModelStoreOwner(
                owner = owner,
                saveableStateHolder = saveableStateHolder
            ) {
                content.Content()
            }

            BackHandler(content.properties.dismissOnBackPress) {
                pop()
            }
        }
    }

    override fun push(key: K, content: (NavigationStackScope<K, V>) -> V) {
        TODO("Not yet implemented")
    }
}

