package com.share.external.lib.mvvm.navigation.switcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.core.ViewProvider
import com.share.external.lib.mvvm.navigation.content.NavigationKey

@Composable
fun <K : NavigationKey> ViewSwitcherHost(
    switcher: ViewSwitcher<K>,
    content: (K, ManagedCoroutineScope) -> ViewProvider
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    remember(switcher.selected) {
        switcher.getOrCreate(content)
    }?.apply {
        setSaveableStateHolder(saveableStateHolder)
        saveableStateHolder.SaveableStateProvider(key = id, content = view.content)
    }
}