package com.getbackcompose.navigation.switcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.core.ViewKey
import com.getbackcompose.core.ViewProvider

fun interface ViewSwitcherContent<K>: (K, ManagedCoroutineScope) -> ViewProvider

/**
 * Hosts a [ViewSwitcher] and renders only the currently selected content.
 *
 * State is preserved via [rememberSaveableStateHolder] when switching between views.
 * Works with both [SingleScopeViewSwitcher] and [RetainingScopeViewSwitcher].
 */
@Composable
fun <K : ViewKey> ViewSwitcherHost(
    switcher: ViewSwitcher<K>,
    content: ViewSwitcherContent<K>,
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    val selectedProvider = remember(switcher.selected, saveableStateHolder) {
        switcher.getOrCreate(content)?.also {
            it.setSaveableStateHolder(saveableStateHolder)
        }
    }

    selectedProvider?.let { provider ->
        key(provider.id) {
            saveableStateHolder.SaveableStateProvider(key = provider.id) {
                provider.view.content()
            }
        }
    }
}
