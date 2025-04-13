package com.share.external.lib.mvvm.navigation.stack

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.share.external.foundation.collections.doublyLinkedMapOf
import com.share.external.foundation.collections.removeLast
import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.foundation.coroutines.MainImmediateScope
import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.foundation.coroutines.childSupervisorJobScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
open class ViewModelNavigationStack<K, V>(
    scope: ManagedCoroutineScope,
) : NavigationStack<K, V> {

    private val providers = doublyLinkedMapOf<K, ViewModelStoreContentProvider<K, V>>()

    protected var currentProvider by mutableStateOf(providers.values.lastOrNull())
        private set

    override var size by mutableIntStateOf(providers.size)
        protected set

    init {
        scope.invokeOnCompletion {
            // Parent scope may complete off main thread.
            MainImmediateScope().launch {
                removeAll()
            }
        }
    }

    override fun push(key: K, content: (NavigationStackScope<K, V>) -> V) {
        TODO("Not yet implemented")
    }


//    override fun push(key: K, content: V) {
//        if (!scope.isActive) {
//            Timber.tag(TAG).wtf("Scope is not active pushing $key, $content onto nav stack: $this")
//        }
//        val previous = providers[key]
//        providers[key] = storeFactory(key, content)
//        updateState()
//        previous?.owner?.clear()
//    }

    override fun pop(): Boolean {
        val removed = providers.removeLast()
        updateState()
        removed?.owner?.clear()
        return removed != null
    }

    override fun popTo(key: K, inclusive: Boolean): Boolean {
        val removed = providers.removeAllAfter(key, inclusive)
        updateState()
        removed.asReversed().forEach { it.owner.clear() }
        return removed.isNotEmpty()
    }

    override fun removeAll() {
        providers.keys.firstOrNull()?.let { popTo(it, true) }
    }

    override fun remove(key: K) {
        val removed = providers.remove(key)
        updateState()
        removed?.owner?.clear()
    }

    private fun updateState() {
        currentProvider = providers.values.lastOrNull()
        size = providers.size
    }

    companion object {
        const val TAG = "ViewModelNavigationStack"
    }
}