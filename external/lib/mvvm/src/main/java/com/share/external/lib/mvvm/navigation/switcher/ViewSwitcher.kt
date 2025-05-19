package com.share.external.lib.mvvm.navigation.switcher

import androidx.compose.runtime.Composable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.content.NavigationKey

interface ViewSwitcher<K : NavigationKey> {
    var selected: K?
    @Composable
    fun Content(content: (K, ManagedCoroutineScope) -> @Composable () -> Unit)
}
