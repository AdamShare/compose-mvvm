package com.share.external.lib.mvvm.navigation.stack

fun interface ViewModelStoreContentFactory<K, V> {
    operator fun invoke(key: K, content: V): ViewModelStoreContentProvider<K, V>
}