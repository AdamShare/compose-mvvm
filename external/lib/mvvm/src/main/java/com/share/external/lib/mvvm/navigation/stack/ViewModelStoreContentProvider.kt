package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Immutable
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.lifecycle.DefaultViewModelStoreOwner

@Immutable
class ViewModelStoreContentProvider<K, V>(
    val key: K,
    val content: V,
    val managedCoroutineScope: ManagedCoroutineScope,
) {
    val owner = DefaultViewModelStoreOwner()
}
